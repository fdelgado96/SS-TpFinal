import random

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

TOTAL_CARS = 200

MAX_TIMESTEPS = 20
INITIAL_TIMESTEP = 20
CARS_CONSIDERED = 8

timesteps = np.arange(1, MAX_TIMESTEPS)

highway_df = pd.read_csv(
    f'../data/cars_{TOTAL_CARS}_maxvel_6_brakeprob_0.2_lanechangeprob_0.5_simulation.csv'
)

cars = []
cars_positions = []
# identify four nearby cars
# pick a random car:
car_0_id = random.randint(0, TOTAL_CARS - 1)
car_0 = highway_df[
    (highway_df['id'] == car_0_id) &
    (highway_df['timestep'] == INITIAL_TIMESTEP)
    ].squeeze()
cars.append(car_0)
cars_positions.append([car_0['lane_position']])
print(f'INITIAL CAR ID: {car_0.id}')
car_ids = [car_0['id']]
for i in range(1, CARS_CONSIDERED):
    cars_behind = highway_df[
        (highway_df['timestep'] == INITIAL_TIMESTEP) &
        (~highway_df['id'].isin(car_ids)) &
        (highway_df['lane_position'] < cars_positions[i - 1][0]) &
        (highway_df['lane'] == cars[i - 1]['lane'])
        ].sort_values(by=['lane_position'])
    car = cars_behind.iloc[[-1], :].squeeze()
    cars.append(car)
    car_ids.append(car['id'])
    cars_positions.append([car['lane_position']])

for time_step in timesteps:
    for car_index in range(0, CARS_CONSIDERED):
        car_position = highway_df[
            (highway_df['timestep'] == INITIAL_TIMESTEP + time_step) &
            (highway_df['id'] == cars[car_index]['id'])
            ].squeeze()['lane_position']

        cars_positions[car_index].append(car_position)

for car_index in range(0, CARS_CONSIDERED):
    plt.plot([i for i in range(INITIAL_TIMESTEP, MAX_TIMESTEPS + INITIAL_TIMESTEP)], cars_positions[car_index],
             'o-', label=f'auto {car_index}')
plt.ylabel(r'posición en la autopista, celda')
plt.xlabel(r'tiempo, segundos')
plt.legend(loc='lower right', prop={'size': 10}, ncol=2)
plt.show()
