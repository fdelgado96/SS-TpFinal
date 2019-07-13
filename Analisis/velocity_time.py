import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('../data/length_50_lanes_5_cars_50_maxvelocity_10_simulation.csv')
times = df['timestep']
velocities = []
for time in times:
    velocities_for_timestep = df.loc[df['timestep'] == time]['velocity']
    velocities.append(velocities_for_timestep.mean())

plt.plot(times, velocities)
plt.show()
