import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

TOTAL_CARS = 200

MAX_TIMESTEPS = 2000
INITIAL_TIMESTEP = 20

timesteps = np.arange(INITIAL_TIMESTEP, MAX_TIMESTEPS + 1, 20)

highway_dfs = []
for i in range(0, 5):
    highway_df = pd.read_csv(
        f'../data/cars_{TOTAL_CARS}_maxvel_6_brakeprob_0.2_lanechangeprob_0.5_simulation_{i}.csv'
    )
    highway_dfs.append(highway_df)

proportions = [
    [
        [], [], [], [], []
    ], [
        [], [], [], [], []
    ], [
        [], [], [], [], []
    ], [
        [], [], [], [], []
    ], [
        [], [], [], [], []
    ]
]

plt.figure(figsize=(9, 3))
for timestep in timesteps:
    for lane in range(0, 5):
        for sample in range(0, 5):
            highway_df = highway_dfs[sample]
            proportion = highway_df[
                (highway_df['lane'] == lane) &
                (highway_df['timestep'] == timestep)
            ]
            proportion = len(proportion.index)
            proportions[lane][sample].append(proportion/TOTAL_CARS)

for lane in range(0, 5):
    proportions[lane] = np.array(proportions[lane])

for lane in range(0, 5):
    plt.errorbar(timesteps, proportions[lane].mean(axis=0), fmt='o-', yerr=proportions[lane].std(axis=0), label=f'carril {lane}')

plt.ylabel(r'proporción de autos en el carril')
plt.xlabel(r'tiempo, segundos')
plt.legend(loc='lower right', prop={'size': 10})
plt.show()
