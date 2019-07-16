import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

highway_dfs_vec = [
    [],
    [],
    [],
    [],
    []
]
densities = []
cars_amounts = np.arange(50, 390, 10)
cars_amounts = np.append(cars_amounts, np.arange(400, 3300, 100))
for sample in range(0, 1):
    for cars_amount in cars_amounts:
        print(f"LOADING FILE FOR CAR AMOUNT: {cars_amount} Sample #{sample}")
        df = pd.read_csv(f'../data/cars_{cars_amount}_maxvel_6_brakeprob_0.2_lanechangeprob_0.5_simulation_{sample}.csv')
        highway_dfs_vec[sample].append(df)
# convert from cars/cell to cars/m
highway_densities = [cars_amount/(666*7.5) for cars_amount in cars_amounts]
fluxes = [
    []
]
for sample in range(0, 1):
    highway_dfs = highway_dfs_vec[sample]
    for i, highway_df in enumerate(highway_dfs):
        print(f'PROCESSING DATAFRAME N°{i}, sample {sample}')
        highway_df = highway_df[highway_df['timestep'] >= 20]
        mean_velocity = highway_df['velocity'].mean() * 7.5   # Convert from cells/second to m/s
        fluxes[sample].append(mean_velocity*(highway_densities[i]))

fluxes = np.array(fluxes)

plt.errorbar(highway_densities, fluxes.mean(axis=0), yerr=fluxes.std(axis=0), fmt='bo')
plt.ylabel(r'flujo del tráfico autos/s')
plt.xlabel(r'densidad del tráfico autos/m')
plt.show()
