import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

TOTAL_CARS = 1500

MAX_TIMESTEPS = 2000
INITIAL_TIMESTEP = 20
MAX_SAMPLES = 3

timesteps = np.arange(INITIAL_TIMESTEP, MAX_TIMESTEPS + 1, 20)

highway_dfs = []
for i in range(0, MAX_SAMPLES):
    highway_df = pd.read_csv(
        f'../data/cars_{TOTAL_CARS}_maxvel_6_brakeprob_0.2_lanechangeprob_0.5_simulation_{i}.csv'
    )
    highway_dfs.append(highway_df)

velocities = [
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
        for sample in range(0, MAX_SAMPLES):
            highway_df = highway_dfs[sample]
            velocity = highway_df[
                (highway_df['lane'] == lane) &
                (highway_df['timestep'] == timestep)
                ]
            velicity_array = np.array(velocity['velocity'].values)
            velocity = velicity_array.mean()*27
            velocities[lane][sample].append(velocity/TOTAL_CARS)

for lane in range(0, 5):
    velocities[lane] = np.array(velocities[lane])


print(velocities[0].mean())
for lane in range(0, 5):
    plt.errorbar(timesteps, velocities[lane].mean(axis=0), fmt='o-', yerr=velocities[lane].std(axis=0), label=f'carril {lane}')

plt.ylabel(r'velocidad promedio de autos en el carril km/hr')
plt.xlabel(r'tiempo, segundos')
plt.legend(loc='lower right', prop={'size': 10})
plt.show()
