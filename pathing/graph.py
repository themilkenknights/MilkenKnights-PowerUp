from plotly import __version__
from plotly.offline import download_plotlyjs, init_notebook_mode, plot, iplot
import plotly.plotly as py
import plotly.graph_objs as go
import plotly.figure_factory as FF

import numpy as np
import pandas as pd

lf = pd.read_csv('LeftPath.csv')
rf = pd.read_csv('RightPath.csv')

lpathtrace = go.Scatter(x=lf['X'], y=lf['Y'], mode='lines', name='Left Path')
rpathtrace = go.Scatter(x=rf['X'], y=rf['Y'], mode='lines', name='Right Path')
mpathtrace = go.Scatter(x=(lf['X'] + rf['X']) / 2, y=(lf['Y'] + rf['Y']) / 2, mode='lines', name='Robot Center Path')

pathlayout = go.Layout(title='Robot Path', plot_bgcolor='rgb(230, 230,230)',xaxis=dict(
	range=[0, 324]
),
yaxis=dict(
	range=[0, -324]
))

pathfig = go.Figure(data=[lpathtrace,rpathtrace,mpathtrace], layout=pathlayout)

plot(pathfig, filename='Robot Path.html')
