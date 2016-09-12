from django.shortcuts import render

# Create your views here.
from django.views.generic import TemplateView
from django.db import connection


class HomeTemplateView(TemplateView):
    template_name = 'base.html'

    def get_context_data(self, **kwargs):
        context = super(HomeTemplateView, self).get_context_data(**kwargs)
        cursor = connection.cursor()
        context['photos'] = cursor.execute("SELECT imgid, category, confidence, url FROM images")
        return context
