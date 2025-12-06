import random
import json

def generate_knapsack_instance(n_items=10, weight_range=(1, 20), value_range=(1, 50), capacity_ratio=0.5):
    weights = [random.randint(*weight_range) for _ in range(n_items)]
    values = [random.randint(*value_range) for _ in range(n_items)]

    total_weight = sum(weights)
    capacity = int(total_weight * capacity_ratio)

    return {
        "n_items": n_items,
        "weights": weights,
        "values": values,
        "capacity": capacity
    }


if __name__ == '__main__':
    seed = 42
    n_instance = 100
    instance_folder = "instances"

    random.seed(seed)

    with open(f"{instance_folder}\\instance_facile_test.json", "w") as file:
        json.dump(generate_knapsack_instance(), file)

    for i in range(n_instance):
        n_items = random.randint(200, 500)
        weight_range = (1, 10_000)
        value_range = (1, 10_000)
        capacity_ratio = random.uniform(0.45, 0.6)

        instance = generate_knapsack_instance(n_items=n_items,
                                              weight_range=weight_range,
                                              value_range=value_range,
                                              capacity_ratio=capacity_ratio)

        filename = f"{i + 1}_{n_items}_{capacity_ratio:.2f}.json"

        with open(f"{instance_folder}\\{filename}", "w") as file:
            json.dump(instance, file)
