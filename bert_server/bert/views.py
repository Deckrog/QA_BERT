import json
import logging

import wikipedia as wiki
from rest_framework import permissions
from rest_framework.response import Response
from rest_framework.views import APIView

logging.getLogger("transformers.tokenization_utils").setLevel(logging.ERROR)

from .compute import DocumentReader
from .serializers import Ask_Serializer


class Components(object):
    def __init__(self, question=None, answer=None):
        self.question = question
        self.answer = answer


reader = DocumentReader("deepset/bert-base-cased-squad2")


class Computation(APIView):
    permission_classes = [permissions.IsAuthenticated]

    @staticmethod
    def post(request):
        q = json.loads(request.body)['question']
        print(q)
        result = wiki.search(q)
        try:
            if len(result) == 0:
                ans = "page not found"
                components = Components(answer=ans)
                serializer = Ask_Serializer(components)
                return Response(serializer.data)
            page = wiki.page(result[0])
            text = page.content
        except wiki.exceptions.DisambiguationError as err:
            components = Components(answer=err)
            serializer = Ask_Serializer(components)
            return Response(serializer.data)
        reader.tokenize(q, text)
        ans = reader.get_answer()
        print(f"Answer: {ans}")
        print()
        components = Components(question=q, answer=ans)
        serializer = Ask_Serializer(components)
        return Response(serializer.data)
