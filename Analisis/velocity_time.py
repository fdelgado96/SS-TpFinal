import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv(f'../data/cars_50_maxvel_6_brakeprob_0.2_lanechangeprob_0.5_simulation.csv')
times = df['timestep']
velocities = []
for time in times:
    velocities_for_timestep = df.loc[df['timestep'] == time]['velocity']
    velocities.append(velocities_for_timestep.mean())

plt.plot(times, velocities)
plt.show()
