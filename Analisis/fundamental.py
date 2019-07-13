import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

highway_dfs = []
densities = []
car_amount = np.arange(50, 2000, 50)
print(car_amount)
highway_dfs.append(pd.read_csv('../data/length_50_lanes_5_cars_50_maxvelocity_10_simulation.csv'))
highway_densities = [50/250]
# convert from cars/cell^2 to cars/km^2
highway_densities = [density*100 for density in highway_densities]
fluxes = []
for i, highway_df in highway_dfs:
    highway_df = highway_df[highway_df['timestep'] >= 20]
    mean_velocity = highway_df['velocity'].mean() * 7.5 * 3.6  # Convert from cells/second to km/hr
    fluxes.append(mean_velocity*highway_densities[i])

plt.plot(highway_densities, fluxes)
plt.ylabel(r'flujo del tráfico \mathrm{autos/hr}')
plt.xlabel(r'densidad del tráfico en porcentaje de celdas ocupadas')
plt.show()
