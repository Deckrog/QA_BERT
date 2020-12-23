from django.urls import path

from . import views


urlpatterns = [
    path('', views.Computation.as_view())
]
