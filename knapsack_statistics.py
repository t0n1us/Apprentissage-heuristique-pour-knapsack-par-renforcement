import json
import os
import matplotlib.pyplot as plt
import numpy as np

results_folder = "résultats\\5epochs"
results = list()

for ms_timelimit_folder in os.listdir(results_folder):
    for result_file in os.listdir(results_folder + "\\" + ms_timelimit_folder):
        with open(results_folder + "\\" + ms_timelimit_folder + "\\" + result_file, "r") as file:
            results.append(json.load(file))

sorted_by_timelimit = sorted(results, key=lambda x: x["ms_timelimit"])
min_ms_timelimit = sorted_by_timelimit[0]["ms_timelimit"]
max_ms_timelimit = sorted_by_timelimit[-1]["ms_timelimit"]

ms_timelimit_range = range(min_ms_timelimit, max_ms_timelimit, 10)
axe_x = list(range(1, 61))

strategies = ["Default", "Heuristic", "RL"]
time_limit_ms = 200

###  INSTANCE / SCORE

plt.figure(1)

# for x in axe_x:
#     plt.axvline(x=x, linewidth=0.5, alpha=0.3)

offsets = {"Default": -0.2, "Heuristic": 0.0, "RL": 0.2}

for strategy in strategies:
    xs, ys = [], []

    for test_id in axe_x:
        for result in results:
            r_test_id = int(result["name"].split("_")[1])
            r_strategy = result["name"].split("_")[0]

            if result["ms_timelimit"] == time_limit_ms and r_test_id == test_id and r_strategy == strategy:
                xs.append(test_id + offsets[strategy])
                ys.append(result["bestValue"])
                break
    plt.plot(
        xs,
        ys,
        alpha=0.3,
        linewidth=1.0
    )
    plt.scatter(xs, ys, label=strategy, alpha=0.7)

plt.xlabel("Instance test")
plt.ylabel("Score")
plt.grid(True)
plt.legend()
plt.show()

### SCATTER DES NOEUDS EXPLORÉS/SCORES

plt.figure(2)

print("Score et noeuds moyens :")
for strategy in strategies:
    xs, ys = [], []

    for result in results:
        r_strategy = result["name"].split("_")[0]
        if result["ms_timelimit"] == time_limit_ms and r_strategy == strategy:
            xs.append(result["nodes"])
            ys.append(result["bestValue"])

    sc = plt.scatter(xs, ys, label=strategy, alpha=0.7)

    color = sc.get_facecolor()[0]

    mean_x = sum(xs) / len(xs)
    mean_y = sum(ys) / len(ys)

    print("Score avg:", strategy, round(mean_y, 2))
    print("Noeuds avg:", strategy, round(mean_x, 2))
    print()

    plt.scatter(
        mean_x,
        mean_y,
        s=250,
        color=color,
        edgecolors="black",
        linewidths=1.5,
        zorder=3
    )

plt.xlabel("Noeuds explorés")
plt.ylabel("Score")
plt.grid(True)
plt.legend()
plt.show()


### QUALITÉ DES NOEUDS

plt.figure(3)

for strategy in strategies:
    quality_ratios = list()

    for test_id in axe_x:
        ratio = 0

        for result in results:
            r_test_id = int(result["name"].split("_")[1])
            r_strategy = result["name"].split("_")[0]

            if result["ms_timelimit"] == time_limit_ms and r_test_id == test_id and r_strategy == strategy:
                ratio = result["bestValue"] / result["nodes"]
                break

        quality_ratios.append(np.log(ratio))

    plt.scatter(axe_x, quality_ratios, label=strategy)
    plt.plot(axe_x, quality_ratios, label=strategy, alpha=0.3, linewidth=1.0)

# plt.title(f"Comparaison du ratio de qualité par noeuds pour chaque stratégies avec un timelimit de {time_limit_ms}ms")
plt.xlabel("Instance test")
plt.ylabel("Log des ratios valeur/noeuds")
plt.grid(True)
plt.legend()
plt.show()

### BOXPLOT REGRET

plt.figure(4)

heuristic_scores = dict()
for result in results:
    if result["ms_timelimit"] == time_limit_ms and result["name"].startswith("Heuristic"):
        test_id = int(result["name"].split("_")[1])
        heuristic_scores[test_id] = result["bestValue"]

regrets = {s: [] for s in strategies if s != "Heuristic"}

for strategy in regrets:
    for result in results:
        r_strategy = result["name"].split("_")[0]
        if result["ms_timelimit"] == time_limit_ms and r_strategy == strategy:
            test_id = int(result["name"].split("_")[1])
            h = heuristic_scores[test_id]
            regrets[strategy].append((h - result["bestValue"]) / h)

plt.boxplot(regrets.values(), labels=regrets.keys())
plt.ylabel("Regret relatif vs heuristique")
plt.grid(True)
plt.show()
