import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

highway_dfs = []
densities = []
cars_amounts = np.arange(50, 1050, 50)
cars_amounts = np.append(cars_amounts, np.arange(1100, 3300, 200))
for cars_amount in cars_amounts:
    df = pd.read_csv(f'../data/cars_{cars_amount}_maxvel_6_brakeprob_0.2_lanechangeprob_0.5_simulation.csv')
    highway_dfs.append(df)
# convert from cars/cell to cars/m
highway_densities = [cars_amount/(666*7.5) for cars_amount in cars_amounts]
fluxes = []
for i, highway_df in enumerate(highway_dfs):
    highway_df = highway_df[highway_df['timestep'] >= 20]
    mean_velocity = highway_df['velocity'].mean() * 7.5   # Convert from cells/second to m/s
    fluxes.append(mean_velocity*(highway_densities[i]))

plt.plot(highway_densities, fluxes, 'bo')
plt.ylabel(r'flujo del tráfico autos/s')
plt.xlabel(r'densidad del tráfico autos/m')
plt.show()
