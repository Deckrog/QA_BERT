from rest_framework import serializers


class Ask_Serializer(serializers.Serializer):
    question = serializers.CharField(max_length=200)
    answer = serializers.CharField(max_length=200)
