"""
Tạo biểu đồ thống kê coverage cho test case Learning Path Generation.

Đọc file CSV test case, phân tích các chiều kiểm thử và tạo biểu đồ
chứng minh mức độ bao phủ của bộ test case đối với tính năng.

Chạy: uv run generate_charts.py
"""

import csv
import re
from collections import Counter
from pathlib import Path

import matplotlib.pyplot as plt
import matplotlib
import numpy as np

# Font hỗ trợ tiếng Việt
matplotlib.rcParams["font.family"] = "sans-serif"
matplotlib.rcParams["font.sans-serif"] = ["DejaVu Sans", "Arial", "Liberation Sans"]

CSV_PATH = Path(__file__).parent / "testcases-learning-path-generation.csv"
OUTPUT_DIR = Path(__file__).parent / "charts"
OUTPUT_DIR.mkdir(exist_ok=True)


# ── Helpers ──────────────────────────────────────────────────────────────────


def parse_csv(path: Path) -> list[dict]:
    rows = []
    with open(path, encoding="utf-8") as f:
        for row in csv.DictReader(f):
            rows.append(row)
    return rows


def extract_priority_order(data_test: str) -> str:
    """Trích xuất hoán vị ưu tiên từ Dữ liệu test."""
    lines = data_test.split("\n")
    priorities = []
    for line in lines:
        m = re.match(r"\s*Ưu tiên #(\d+):\s*(.+)", line)
        if m:
            rank = int(m.group(1))
            label = m.group(2).strip()
            if "GPA" in label or "Đạt GPA" in label:
                priorities.append((rank, "GPA"))
            elif "Nghề nghiệp" in label or "Kiến thức" in label:
                priorities.append((rank, "Nghề"))
            elif "Ra trường" in label:
                priorities.append((rank, "Đúng hạn"))
    priorities.sort(key=lambda x: x[0])
    order = "".join(p[1] for p in priorities)
    mapping = {
        "GPANghềĐúng hạn": "GPA > Nghề > Đúng hạn",
        "GPAĐúng hạnNghề": "GPA > Đúng hạn > Nghề",
        "NghềGPAĐúng hạn": "Nghề > GPA > Đúng hạn",
        "NghềĐúng hạnGPA": "Nghề > Đúng hạn > GPA",
        "Đúng hạnGPANghề": "Đúng hạn > GPA > Nghề",
        "Đúng hạnNghềGPA": "Đúng hạn > Nghề > GPA",
    }
    return mapping.get(order, f"Khác ({order})")


PRIORITY_SHORT = {
    "GPA > Nghề > Đúng hạn": "GP-Ngh-ĐH",
    "GPA > Đúng hạn > Nghề": "GP-ĐH-Ngh",
    "Nghề > GPA > Đúng hạn": "Ngh-GP-ĐH",
    "Nghề > Đúng hạn > GPA": "Ngh-ĐH-GP",
    "Đúng hạn > GPA > Nghề": "ĐH-GP-Ngh",
    "Đúng hạn > Nghề > GPA": "ĐH-Ngh-GP",
}

PRIORITY_FULL = {
    "GPA > Nghề > Đúng hạn": "1. GPA\n2. Nghề nghiệp\n3. Ra trường đúng hạn",
    "GPA > Đúng hạn > Nghề": "1. GPA\n2. Ra trường đúng hạn\n3. Nghề nghiệp",
    "Nghề > GPA > Đúng hạn": "1. Nghề nghiệp\n2. GPA\n3. Ra trường đúng hạn",
    "Nghề > Đúng hạn > GPA": "1. Nghề nghiệp\n2. Ra trường đúng hạn\n3. GPA",
    "Đúng hạn > GPA > Nghề": "1. Ra trường đúng hạn\n2. GPA\n3. Nghề nghiệp",
    "Đúng hạn > Nghề > GPA": "1. Ra trường đúng hạn\n2. Nghề nghiệp\n3. GPA",
}


def extract_intensity(data_test: str) -> str:
    if "Cường độ HK chính: Low" in data_test:
        return "Thấp"
    if "Cường độ HK chính: Light" in data_test:
        return "Nhẹ nhàng"
    if "Cường độ HK chính: Standard" in data_test:
        return "Trung bình"
    if "Cường độ HK chính: Heavy" in data_test:
        return "Cao"
    return "Không xác định"


def extract_gpa_target(data_test: str) -> str:
    m = re.search(r"GPA mục tiêu:\s*([\d.]+)", data_test)
    return m.group(1) if m else "Không xác định"


def extract_occupation(data_test: str) -> str:
    m = re.search(r"Nghề nghiệp:\s*(.+)", data_test)
    if not m:
        return "Không xác định"
    val = m.group(1).strip()
    if "(không chọn)" in val:
        return "Không chọn nghề"
    parts = val.split(" - ")
    if len(parts) >= 2:
        return parts[1].strip()
    return val.strip()


def extract_summer_semesters(data_test: str) -> str:
    m = re.search(r"Số HK hè:\s*(\d+)", data_test)
    if not m:
        return "Không xác định"
    count = int(m.group(1))
    if count == 0:
        return "Không học hè"
    if "cường độ: Heavy" in data_test:
        return f"{count} HK hè (Cao)"
    if "cường độ: Standard" in data_test:
        return f"{count} HK hè (Trung bình)"
    if "cường độ: Light" in data_test:
        return f"{count} HK hè (Nhẹ)"
    return f"{count} HK hè"


def extract_feasibility(expected: str) -> str:
    expected_lower = expected.lower()
    if "weak" in expected_lower and "medium" in expected_lower:
        return "Trung bình / Yếu"
    if "good" in expected_lower and "medium" in expected_lower:
        return "Trung bình / Tốt"
    if "weak" in expected_lower:
        return "Yếu"
    if "good" in expected_lower:
        return "Tốt"
    if "medium" in expected_lower:
        return "Trung bình"
    return "Không xác định"


# ── Vietnamese labels ─────────────────────────────────────────────────────────

SCENARIO_NAMES = {
    "S01": "S01: Mẫu chuẩn",
    "S02": "S02: Mới nhập học",
    "S03": "S03: Chỉ năm nhất",
    "S04": "S04: Chỉ năm 1-2",
    "S05": "S05: GPA cao",
    "S06": "S06: GPA thấp",
    "S07": "S07: Gần hoàn thành",
    "S08": "S08: Thiếu môn tiên quyết",
    "S09": "S09: Giỏi đại cương,\nyếu chuyên ngành",
    "S10": "S10: Có học hè",
    "S11": "S11: Bỏ năm (gap year)",
    "S12": "S12: Có môn rớt",
}

INTENSITY_ORDER = ["Thấp", "Nhẹ nhàng", "Trung bình", "Cao"]
PRIORITY_ORDER = [
    "GPA > Nghề > Đúng hạn",
    "GPA > Đúng hạn > Nghề",
    "Nghề > GPA > Đúng hạn",
    "Nghề > Đúng hạn > GPA",
    "Đúng hạn > GPA > Nghề",
    "Đúng hạn > Nghề > GPA",
]
FEASIBILITY_ORDER = ["Tốt", "Trung bình / Tốt", "Trung bình", "Trung bình / Yếu", "Yếu", "Không xác định"]
GPA_ORDER = ["2.0", "2.5", "3.0", "3.5", "4.0"]
TEST_PRIORITY_ORDER = ["Rất cao", "Cao", "Trung bình"]


# ── Color palette ─────────────────────────────────────────────────────────────

PALETTE = {
    "blue": "#3B82F6",
    "green": "#22C55E",
    "orange": "#F97316",
    "red": "#EF4444",
    "purple": "#8B5CF6",
    "teal": "#14B8A6",
    "pink": "#EC4899",
    "amber": "#F59E0B",
    "indigo": "#6366F1",
    "cyan": "#06B6D4",
    "lime": "#84CC16",
    "rose": "#F43F5E",
}
BAR_COLORS = [
    PALETTE["blue"], PALETTE["green"], PALETTE["orange"], PALETTE["purple"],
    PALETTE["teal"], PALETTE["pink"], PALETTE["amber"], PALETTE["indigo"],
    PALETTE["cyan"], PALETTE["lime"], PALETTE["rose"], PALETTE["red"],
]


def save(fig, name):
    path = OUTPUT_DIR / name
    fig.savefig(path, dpi=150, bbox_inches="tight", facecolor="white")
    plt.close(fig)
    print(f"  ✓ {path}")


# ── Chart 1: Bao phủ theo Kịch bản Sinh viên ─────────────────────────────────


def chart_scenario_coverage(data):
    scenarios = Counter(r["Scenario ID"] for r in data)
    # Sắp xếp theo thứ tự S01-S12
    sorted_keys = sorted(scenarios.keys())
    labels = [SCENARIO_NAMES.get(s, s) for s in sorted_keys]
    values = [scenarios[s] for s in sorted_keys]

    fig, ax = plt.subplots(figsize=(12, 6))
    bars = ax.barh(labels, values, color=BAR_COLORS[: len(labels)], edgecolor="white")
    ax.bar_label(bars, fontsize=10, fontweight="bold", padding=3)
    ax.set_xlabel("Số lượng test case", fontsize=12)
    ax.set_title("Kịch bản Sinh viên", fontsize=14, fontweight="bold")
    ax.invert_yaxis()
    ax.set_xlim(0, max(values) + 2)
    for spine in ["top", "right"]:
        ax.spines[spine].set_visible(False)
    fig.tight_layout()
    save(fig, "01_scenario_coverage.png")


# ── Chart 2: Bao phủ theo Thứ tự Ưu tiên Học tập ────────────────────────────


def chart_priority_coverage(data):
    priorities = [extract_priority_order(r["Dữ liệu test"]) for r in data]
    counts = Counter(priorities)
    labels = [p for p in PRIORITY_ORDER if p in counts]
    values = [counts[p] for p in labels]

    # Dùng horizontal bar để tránh chữ chồng lên nhau
    fig, ax = plt.subplots(figsize=(11, 5))
    colors = [
        PALETTE["blue"], PALETTE["green"], PALETTE["orange"],
        PALETTE["purple"], PALETTE["teal"], PALETTE["pink"],
    ]
    bars = ax.barh(labels, values, color=colors[: len(labels)], edgecolor="white", height=0.55)
    ax.bar_label(bars, fontsize=10, fontweight="bold", padding=3)
    ax.set_xlabel("Số lượng test case", fontsize=12)
    ax.set_title("Thứ tự Ưu tiên Học tập", fontsize=14, fontweight="bold")
    ax.invert_yaxis()
    ax.set_xlim(0, max(values) + 2)
    for spine in ["top", "right"]:
        ax.spines[spine].set_visible(False)
    fig.tight_layout()
    save(fig, "02_priority_order_coverage.png")


# ── Chart 3: Bao phủ theo Cường độ Học tập ───────────────────────────────────


def chart_intensity_coverage(data):
    intensities = [extract_intensity(r["Dữ liệu test"]) for r in data]
    counts = Counter(intensities)
    labels = [i for i in INTENSITY_ORDER if i in counts]
    values = [counts[i] for i in labels]

    colors = [PALETTE["blue"], PALETTE["green"], PALETTE["orange"], PALETTE["red"]]
    fig, ax = plt.subplots(figsize=(8, 5))
    bars = ax.bar(labels, values, color=colors[: len(labels)], edgecolor="white", width=0.5)
    ax.bar_label(bars, fontsize=10, fontweight="bold", padding=3)
    ax.set_ylabel("Số lượng test case", fontsize=12)
    ax.set_title("Cường độ Học tập", fontsize=14, fontweight="bold")
    for spine in ["top", "right"]:
        ax.spines[spine].set_visible(False)
    fig.tight_layout()
    save(fig, "03_intensity_coverage.png")


# ── Chart 4: Bao phủ theo GPA Mục tiêu ──────────────────────────────────────


def chart_gpa_coverage(data):
    gpas = [extract_gpa_target(r["Dữ liệu test"]) for r in data]
    counts = Counter(gpas)
    labels = [g for g in GPA_ORDER if g in counts]
    values = [counts[g] for g in labels]

    fig, ax = plt.subplots(figsize=(8, 5))
    colors = [PALETTE["green"], PALETTE["teal"], PALETTE["blue"], PALETTE["orange"], PALETTE["red"]]
    bars = ax.bar(labels, values, color=colors[: len(labels)], edgecolor="white", width=0.5)
    ax.bar_label(bars, fontsize=10, fontweight="bold", padding=3)
    ax.set_xlabel("GPA mục tiêu (thang 4.0)", fontsize=12)
    ax.set_ylabel("Số lượng test case", fontsize=12)
    ax.set_title("GPA Mục tiêu", fontsize=14, fontweight="bold")
    for spine in ["top", "right"]:
        ax.spines[spine].set_visible(False)
    fig.tight_layout()
    save(fig, "04_gpa_target_coverage.png")


# ── Chart 5: Bao phủ theo Định hướng Nghề nghiệp ─────────────────────────────


def chart_occupation_coverage(data):
    occupations = [extract_occupation(r["Dữ liệu test"]) for r in data]
    counts = Counter(occupations)
    sorted_items = sorted(counts.items(), key=lambda x: (-x[1], x[0]))
    labels = [item[0] for item in sorted_items]
    values = [item[1] for item in sorted_items]

    fig, ax = plt.subplots(figsize=(10, 5))
    occ_colors = [PALETTE["amber"] if l == "Không chọn nghề" else PALETTE["purple"] for l in labels]
    bars = ax.barh(labels, values, color=occ_colors, edgecolor="white")
    ax.bar_label(bars, fontsize=10, fontweight="bold", padding=3)
    ax.set_xlabel("Số lượng test case", fontsize=12)
    ax.set_title("Định hướng Nghề nghiệp", fontsize=14, fontweight="bold")
    ax.invert_yaxis()
    ax.set_xlim(0, max(values) + 4)
    for spine in ["top", "right"]:
        ax.spines[spine].set_visible(False)
    fig.tight_layout()
    save(fig, "05_occupation_coverage.png")


# ── Chart 6: Bao phủ theo Kế hoạch Học hè ────────────────────────────────────


def chart_summer_coverage(data):
    summers = [extract_summer_semesters(r["Dữ liệu test"]) for r in data]
    counts = Counter(summers)
    sorted_items = sorted(counts.items(), key=lambda x: x[1], reverse=True)
    labels = [item[0] for item in sorted_items]
    values = [item[1] for item in sorted_items]

    fig, ax = plt.subplots(figsize=(10, 5))
    summer_colors = {
        "Không học hè": PALETTE["red"],
        "1 HK hè (Trung bình)": PALETTE["blue"],
        "1 HK hè (Cao)": PALETTE["orange"],
        "1 HK hè (Nhẹ)": PALETTE["green"],
        "3 HK hè (Cao)": PALETTE["purple"],
        "3 HK hè (Trung bình)": PALETTE["teal"],
    }
    colors = [summer_colors.get(l, PALETTE["cyan"]) for l in labels]
    bars = ax.barh(labels, values, color=colors, edgecolor="white")
    ax.bar_label(bars, fontsize=10, fontweight="bold", padding=3)
    ax.set_xlabel("Số lượng test case", fontsize=12)
    ax.set_title("Kế hoạch Học hè", fontsize=14, fontweight="bold")
    ax.invert_yaxis()
    ax.set_xlim(0, max(values) + 4)
    for spine in ["top", "right"]:
        ax.spines[spine].set_visible(False)
    fig.tight_layout()
    save(fig, "06_summer_semester_coverage.png")


# ── Chart 7: Bao phủ theo Mức Khả thi ────────────────────────────────────────


def chart_feasibility_coverage(data):
    feasibilities = [extract_feasibility(r["Kết quả mong đợi"]) for r in data]
    counts = Counter(feasibilities)
    labels = [f for f in FEASIBILITY_ORDER if f in counts]
    values = [counts[f] for f in labels]

    feasibility_colors = {
        "Tốt": PALETTE["green"],
        "Trung bình / Tốt": PALETTE["teal"],
        "Trung bình": PALETTE["blue"],
        "Trung bình / Yếu": PALETTE["orange"],
        "Yếu": PALETTE["red"],
        "Không xác định": PALETTE["amber"],
    }
    colors = [feasibility_colors.get(l, PALETTE["cyan"]) for l in labels]

    fig, ax = plt.subplots(figsize=(8, 5))
    bars = ax.bar(labels, values, color=colors, edgecolor="white", width=0.5)
    ax.bar_label(bars, fontsize=10, fontweight="bold", padding=3)
    ax.set_ylabel("Số lượng test case", fontsize=12)
    ax.set_title("Mức Khả thi", fontsize=14, fontweight="bold")
    ax.tick_params(axis="x", labelsize=9)
    for spine in ["top", "right"]:
        ax.spines[spine].set_visible(False)
    fig.tight_layout()
    save(fig, "07_feasibility_coverage.png")


# ── Chart 8: Phân bổ Mức độ Ưu tiên Test Case ────────────────────────────────


def chart_test_priority(data):
    priority_map = {"Critical": "Rất cao", "High": "Cao", "Medium": "Trung bình"}
    priorities = Counter(priority_map.get(r["Mức độ ưu tiên"], r["Mức độ ưu tiên"]) for r in data)
    labels = TEST_PRIORITY_ORDER
    values = [priorities.get(l, 0) for l in labels]
    total = sum(values)
    percentages = [v / total * 100 for v in values]

    pie_colors = [PALETTE["red"], PALETTE["orange"], PALETTE["blue"]]
    fig, ax = plt.subplots(figsize=(7, 7))
    ax.pie(
        values,
        labels=[f"{l}\n({v} TC, {p:.1f}%)" for l, v, p in zip(labels, values, percentages)],
        colors=pie_colors,
        startangle=90,
        textprops={"fontsize": 12},
    )
    ax.set_title("Phân bổ Mức độ Ưu tiên Test Case", fontsize=14, fontweight="bold")
    fig.tight_layout()
    save(fig, "08_test_priority_distribution.png")


# ── Chart 9: Ma trận Kịch bản × Thứ tự Ưu tiên ────────────────────────────────


def chart_scenario_priority_heatmap(data):
    matrix = Counter()
    for r in data:
        scenario = r["Scenario ID"]
        priority = extract_priority_order(r["Dữ liệu test"])
        matrix[(scenario, priority)] += 1

    scenario_labels = sorted(set(r["Scenario ID"] for r in data))
    priority_labels = PRIORITY_ORDER

    arr = np.zeros((len(scenario_labels), len(priority_labels)))
    for i, s in enumerate(scenario_labels):
        for j, p in enumerate(priority_labels):
            arr[i][j] = matrix.get((s, p), 0)

    fig, ax = plt.subplots(figsize=(14, 7))
    im = ax.imshow(arr, cmap="YlOrRd", aspect="auto")

    # Nhãn cột: dạng ngắn gọn để không chồng
    short_priority = [PRIORITY_SHORT.get(p, p) for p in priority_labels]
    ax.set_xticks(range(len(priority_labels)))
    ax.set_xticklabels(short_priority, rotation=0, ha="center", fontsize=10)
    ax.set_yticks(range(len(scenario_labels)))
    ax.set_yticklabels([SCENARIO_NAMES.get(s, s).replace("\n", " ") for s in scenario_labels], fontsize=9)

    for i in range(len(scenario_labels)):
        for j in range(len(priority_labels)):
            val = int(arr[i][j])
            if val > 0:
                ax.text(j, i, str(val), ha="center", va="center", fontsize=10, fontweight="bold",
                        color="white" if val > 1 else "black")

    ax.set_title("Ma trận: Kịch bản Sinh viên × Thứ tự Ưu tiên",
                 fontsize=14, fontweight="bold")
    cbar = fig.colorbar(im, ax=ax, shrink=0.8)
    cbar.set_label("Số test case", fontsize=11)
    fig.tight_layout()
    save(fig, "09_scenario_priority_heatmap.png")


# ── Chart 10: Ma trận Kịch bản × Cường độ Học tập ────────────────────────────


def chart_scenario_intensity_heatmap(data):
    matrix = Counter()
    for r in data:
        scenario = r["Scenario ID"]
        intensity = extract_intensity(r["Dữ liệu test"])
        matrix[(scenario, intensity)] += 1

    scenario_labels = sorted(set(r["Scenario ID"] for r in data))
    intensity_labels = INTENSITY_ORDER

    arr = np.zeros((len(scenario_labels), len(intensity_labels)))
    for i, s in enumerate(scenario_labels):
        for j, it in enumerate(intensity_labels):
            arr[i][j] = matrix.get((s, it), 0)

    fig, ax = plt.subplots(figsize=(10, 7))
    im = ax.imshow(arr, cmap="Blues", aspect="auto")

    ax.set_xticks(range(len(intensity_labels)))
    ax.set_xticklabels(intensity_labels, fontsize=11)
    ax.set_yticks(range(len(scenario_labels)))
    ax.set_yticklabels([SCENARIO_NAMES.get(s, s).replace("\n", " ") for s in scenario_labels], fontsize=9)

    for i in range(len(scenario_labels)):
        for j in range(len(intensity_labels)):
            val = int(arr[i][j])
            if val > 0:
                ax.text(j, i, str(val), ha="center", va="center", fontsize=10, fontweight="bold",
                        color="white" if val > 2 else "black")

    ax.set_title("Ma trận: Kịch bản Sinh viên × Cường độ Học tập",
                 fontsize=14, fontweight="bold")
    cbar = fig.colorbar(im, ax=ax, shrink=0.8)
    cbar.set_label("Số test case", fontsize=11)
    fig.tight_layout()
    save(fig, "10_scenario_intensity_heatmap.png")


# ── Chart 11: Tổng hợp Bao phủ ────────────────────────────────────────────────


def chart_coverage_summary(data):
    dims = {
        "Kịch bản\nsinh viên\n(12)": len(set(r["Scenario ID"] for r in data)),
        "Thứ tự\nưu tiên\n(6 hoán vị)": len(set(extract_priority_order(r["Dữ liệu test"]) for r in data)),
        "Cường độ\nhọc tập\n(4 mức)": len(set(extract_intensity(r["Dữ liệu test"]) for r in data)),
        "GPA\nmục tiêu\n(5 mức)": len(set(extract_gpa_target(r["Dữ liệu test"]) for r in data)),
        "Nghề\nnghiệp\n(5 loại)": len(set(extract_occupation(r["Dữ liệu test"]) for r in data)),
        "Kế hoạch\nhọc hè\n(3 loại)": len(set(extract_summer_semesters(r["Dữ liệu test"]) for r in data)),
        "Mức\nkhả thi\n(5 mức)": len(set(extract_feasibility(r["Kết quả mong đợi"]) for r in data)),
    }
    max_vals = {
        "Kịch bản\nsinh viên\n(12)": 12,
        "Thứ tự\nưu tiên\n(6 hoán vị)": 6,
        "Cường độ\nhọc tập\n(4 mức)": 4,
        "GPA\nmục tiêu\n(5 mức)": 5,
        "Nghề\nnghiệp\n(5 loại)": 5,
        "Kế hoạch\nhọc hè\n(3 loại)": 3,
        "Mức\nkhả thi\n(5 mức)": 5,
    }

    labels = list(dims.keys())
    actual = [dims[l] for l in labels]
    maximum = [max_vals[l] for l in labels]
    pct = [a / m * 100 for a, m in zip(actual, maximum)]

    fig, ax = plt.subplots(figsize=(12, 6))
    x = np.arange(len(labels))
    width = 0.35
    bars1 = ax.bar(x - width / 2, maximum, width, label="Tổng số giá trị", color="#E5E7EB", edgecolor="white")
    bars2 = ax.bar(x + width / 2, actual, width, label="Đã bao phủ", color=PALETTE["blue"], edgecolor="white")

    for i, (bar, p) in enumerate(zip(bars2, pct)):
        ax.text(bar.get_x() + bar.get_width() / 2, bar.get_height() + 0.2,
                f"{actual[i]}/{maximum[i]}\n({p:.0f}%)",
                ha="center", va="bottom", fontsize=9, fontweight="bold")

    ax.set_xticks(x)
    ax.set_xticklabels(labels, fontsize=9)
    ax.set_ylabel("Số giá trị duy nhất", fontsize=12)
    ax.set_title("Tổng hợp Các Chiều Kiểm thử", fontsize=14, fontweight="bold")
    ax.legend(fontsize=11)
    ax.set_ylim(0, max(maximum) + 3)
    for spine in ["top", "right"]:
        ax.spines[spine].set_visible(False)
    fig.tight_layout()
    save(fig, "11_coverage_summary.png")


# ── Main ──────────────────────────────────────────────────────────────────────


def main():
    print(f"Đọc file CSV: {CSV_PATH}")
    data = parse_csv(CSV_PATH)
    print(f"  → {len(data)} test case\n")

    print("Tạo biểu đồ...")
    chart_scenario_coverage(data)
    chart_priority_coverage(data)
    chart_intensity_coverage(data)
    chart_gpa_coverage(data)
    chart_occupation_coverage(data)
    chart_summer_coverage(data)
    chart_feasibility_coverage(data)
    chart_test_priority(data)
    chart_scenario_priority_heatmap(data)
    chart_scenario_intensity_heatmap(data)
    chart_coverage_summary(data)

    print(f"\nHoàn thành! {len(list(OUTPUT_DIR.glob('*.png')))} biểu đồ đã được lưu tại: {OUTPUT_DIR}")


if __name__ == "__main__":
    main()