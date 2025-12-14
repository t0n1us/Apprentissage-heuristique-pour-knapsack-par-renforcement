import math
import matplotlib.pyplot as plt


def epsilon(t, epsilon_0=1.0, epsilon_min=0.0, epsilon_decay=1e-3):
    return epsilon_min + (epsilon_0 - epsilon_min) * math.exp(-epsilon_decay * t)

eps_slow = list()
eps_medium = list()
eps_fast = list()

ts = list(range(5000))

for t in ts:
    eps_slow.append(epsilon(t, epsilon_decay=5e-4))
    eps_medium.append(epsilon(t))
    eps_fast.append(epsilon(t, epsilon_decay=5e-3))

plt.figure()
plt.plot(ts, eps_slow, label="e=5e-4")
plt.plot(ts, eps_medium, label="e=1e-3")
plt.plot(ts, eps_fast, label="e=5e-3")
plt.xlabel("Épisode")
plt.ylabel("Epsilon")
plt.title("Décroissance exponentielle de epsilon")
plt.legend()
plt.grid(True)
plt.show()


