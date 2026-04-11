package com.github.iappapp.panda.common.visualization;

import com.github.iappapp.panda.common.enums.AlgorithmType;
import com.github.iappapp.panda.common.enums.OpType;
import com.github.iappapp.panda.common.operation.SortOperation;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 常见排序可视化（Swing）
 *
 * 特性：
 * 1. 每秒推进一次排序步骤
 * 2. 交换时进行平滑移动动画
 * 3. 支持常见排序算法切换
 */
public class SortVisualizerApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SortVisualizerApp::createAndShowUI);
    }

    private static void createAndShowUI() {
        JFrame frame = new JFrame("排序可视化");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        SortVisualizerPanel panel = new SortVisualizerPanel();

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(248, 249, 251));
        JLabel label = new JLabel("算法");
        JComboBox<AlgorithmType> algorithmBox = new JComboBox<>(AlgorithmType.values());
        JButton resetButton = new JButton("重新开始");

        topPanel.add(label);
        topPanel.add(algorithmBox);
        topPanel.add(resetButton);

        algorithmBox.addActionListener(event -> panel.restart((AlgorithmType) algorithmBox.getSelectedItem()));
        resetButton.addActionListener(event -> panel.restart((AlgorithmType) algorithmBox.getSelectedItem()));

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.restart(AlgorithmType.SELECTION);
    }

    private static int[] randomData(int size, int min, int max) {
        int[] values = new int[size];
        Random random = new Random();
        for (int index = 0; index < size; index++) {
            values[index] = random.nextInt(max - min + 1) + min;
        }
        return values;
    }

    private static class SortVisualizerPanel extends JPanel {
        private static final int PANEL_WIDTH = 980;
        private static final int PANEL_HEIGHT = 560;
        private static final int TOP_MARGIN = 90;
        private static final int BOTTOM_MARGIN = 80;
        private static final int SIDE_MARGIN = 40;
        private static final int STEP_INTERVAL_MS = 1000;
        private static final int ANIMATION_FPS = 25;
        private static final int DATA_SIZE = 14;

        private int[] values;
        private int barWidth;
        private int gap;
        private List<SortOperation> operations = new ArrayList<SortOperation>();
        private int operationIndex = 0;
        private AlgorithmType algorithmType = AlgorithmType.SELECTION;

        private final Timer stepTimer;
        private Timer animationTimer;

        private int highlightA = -1;
        private int highlightB = -1;
        private int highlightC = -1;

        private int swapLeftIndex = -1;
        private int swapRightIndex = -1;
        private float swapProgress = 0f;

        private int moveFromIndex = -1;
        private int moveToIndex = -1;
        private int moveValue = 0;
        private float moveProgress = 0f;

        private boolean animating = false;
        private boolean sorted = false;

        private String statusText = "开始";

        private SortVisualizerPanel() {
            this.values = randomData(DATA_SIZE, 10, 99);
            setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
            setBackground(new Color(248, 249, 251));

            recalcLayoutMetrics();

            this.stepTimer = new Timer(STEP_INTERVAL_MS, event -> step());
            this.stepTimer.start();
        }

        private void recalcLayoutMetrics() {
            int availableWidth = PANEL_WIDTH - SIDE_MARGIN * 2;
            this.gap = 8;
            this.barWidth = (availableWidth - gap * (values.length - 1)) / values.length;
        }

        private void restart(AlgorithmType type) {
            if (type != null) {
                this.algorithmType = type;
            }
            if (animationTimer != null) {
                animationTimer.stop();
                animationTimer = null;
            }
            this.values = randomData(DATA_SIZE, 10, 99);
            recalcLayoutMetrics();

            this.operations = SortOperationFactory.generate(this.algorithmType, this.values);
            this.operationIndex = 0;
            this.highlightA = -1;
            this.highlightB = -1;
            this.highlightC = -1;

            this.swapLeftIndex = -1;
            this.swapRightIndex = -1;
            this.swapProgress = 0f;

            this.moveFromIndex = -1;
            this.moveToIndex = -1;
            this.moveValue = 0;
            this.moveProgress = 0f;

            this.animating = false;
            this.sorted = false;
            this.statusText = "开始：" + this.algorithmType.getDisplayName();
            repaint();
        }

        private void step() {
            if (sorted || animating) {
                return;
            }

            if (operationIndex >= operations.size()) {
                sorted = true;
                statusText = "排序完成";
                repaint();
                return;
            }

            SortOperation operation = operations.get(operationIndex++);
            highlightA = operation.a;
            highlightB = operation.b;
            highlightC = operation.c;
            statusText = operation.message;

            if (operation.type == OpType.SWAP) {
                startSwapAnimation(operation.a, operation.b, operation.message);
                return;
            }
            if (operation.type == OpType.SET) {
                if (operation.fromIndex >= 0) {
                    startMoveAnimation(operation.fromIndex, operation.a, operation.value, operation.message);
                    return;
                }
                values[operation.a] = operation.value;
            } else if (operation.type == OpType.DONE) {
                sorted = true;
            }

            repaint();
        }

        private void startSwapAnimation(int leftIndex, int rightIndex, String message) {
            animating = true;
            swapLeftIndex = leftIndex;
            swapRightIndex = rightIndex;
            swapProgress = 0f;
            statusText = message;

            int frameDelayMs = 1000 / ANIMATION_FPS;
            animationTimer = new Timer(frameDelayMs, event -> {
                float delta = 1f / ANIMATION_FPS;
                swapProgress += delta;
                if (swapProgress >= 1f) {
                    swapProgress = 1f;
                    finishSwapAnimation();
                }
                repaint();
            });
            animationTimer.start();
        }

        private void startMoveAnimation(int fromIndex, int toIndex, int value, String message) {
            animating = true;
            moveFromIndex = fromIndex;
            moveToIndex = toIndex;
            moveValue = value;
            moveProgress = 0f;
            statusText = message;

            int frameDelayMs = 1000 / ANIMATION_FPS;
            animationTimer = new Timer(frameDelayMs, event -> {
                float delta = 1f / ANIMATION_FPS;
                moveProgress += delta;
                if (moveProgress >= 1f) {
                    moveProgress = 1f;
                    finishMoveAnimation();
                }
                repaint();
            });
            animationTimer.start();
        }

        private void finishSwapAnimation() {
            if (animationTimer != null) {
                animationTimer.stop();
                animationTimer = null;
            }

            int temp = values[swapLeftIndex];
            values[swapLeftIndex] = values[swapRightIndex];
            values[swapRightIndex] = temp;

            animating = false;
            swapLeftIndex = -1;
            swapRightIndex = -1;
            swapProgress = 0f;

            repaint();
        }

        private void finishMoveAnimation() {
            if (animationTimer != null) {
                animationTimer.stop();
                animationTimer = null;
            }
            values[moveToIndex] = moveValue;

            animating = false;
            moveFromIndex = -1;
            moveToIndex = -1;
            moveProgress = 0f;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawTitle(g2d);
            drawBars(g2d);
            drawStatus(g2d);
        }

        private void drawTitle(Graphics2D g2d) {
            g2d.setColor(new Color(33, 37, 41));
            g2d.setFont(new Font("PingFang SC", Font.BOLD, 20));
            g2d.drawString("排序可视化（每秒一步）", 30, 56);
            g2d.setFont(new Font("PingFang SC", Font.PLAIN, 16));
            g2d.drawString("当前算法：" + algorithmType.getDisplayName(), 30, 82);
        }

        private void drawStatus(Graphics2D g2d) {
            g2d.setColor(new Color(73, 80, 87));
            g2d.setFont(new Font("PingFang SC", Font.PLAIN, 16));
            g2d.drawString(statusText, 30, PANEL_HEIGHT - 30);
        }

        private void drawBars(Graphics2D g2d) {
            int maxValue = getMaxValue(values);
            int maxHeight = PANEL_HEIGHT - TOP_MARGIN - BOTTOM_MARGIN;

            for (int index = 0; index < values.length; index++) {
                int value = values[index];
                int height = (int) (value * 1.0 / maxValue * maxHeight);

                float x = getBaseX(index);
                if (animating) {
                    if (index == swapLeftIndex) {
                        x = lerp(getBaseX(swapLeftIndex), getBaseX(swapRightIndex), swapProgress);
                    } else if (index == swapRightIndex) {
                        x = lerp(getBaseX(swapRightIndex), getBaseX(swapLeftIndex), swapProgress);
                    }
                }

                int barX = Math.round(x);
                int barY = PANEL_HEIGHT - BOTTOM_MARGIN - height;

                g2d.setColor(colorForIndex(index));
                g2d.fillRoundRect(barX, barY, barWidth, height, 14, 14);

                g2d.setColor(new Color(73, 80, 87));
                g2d.setFont(new Font("PingFang SC", Font.PLAIN, 13));
                g2d.drawString(String.valueOf(value), barX + barWidth / 2 - 9, barY - 8);
                g2d.drawString(String.valueOf(index), barX + barWidth / 2 - 5, PANEL_HEIGHT - BOTTOM_MARGIN + 20);
            }

            if (animating && moveFromIndex >= 0 && moveToIndex >= 0) {
                int maxMoveHeight = (int) (moveValue * 1.0 / maxValue * maxHeight);
                float moveX = lerp(getBaseX(moveFromIndex), getBaseX(moveToIndex), moveProgress);
                int moveY = PANEL_HEIGHT - BOTTOM_MARGIN - maxMoveHeight;
                g2d.setColor(new Color(255, 159, 67));
                g2d.fillRoundRect(Math.round(moveX), moveY, barWidth, maxMoveHeight, 14, 14);
                g2d.setColor(new Color(73, 80, 87));
                g2d.drawString(String.valueOf(moveValue), Math.round(moveX) + barWidth / 2 - 9, moveY - 8);
            }
        }

        private Color colorForIndex(int index) {
            if (sorted) {
                return new Color(46, 204, 113);
            }
            if (index == highlightC) {
                return new Color(250, 173, 20);
            }
            if (index == highlightA || index == highlightB) {
                return new Color(24, 144, 255);
            }
            if (animating && (index == swapLeftIndex || index == swapRightIndex)) {
                return new Color(255, 159, 67);
            }
            return new Color(114, 132, 154);
        }

        private int getMaxValue(int[] array) {
            int max = array[0];
            for (int value : array) {
                if (value > max) {
                    max = value;
                }
            }
            return max;
        }

        private int getBaseX(int index) {
            return SIDE_MARGIN + index * (barWidth + gap);
        }

        private float lerp(float start, float end, float progress) {
            return start + (end - start) * progress;
        }
    }







    private static class SortOperationFactory {
        private static List<SortOperation> generate(AlgorithmType type, int[] source) {
            int[] arr = Arrays.copyOf(source, source.length);
            List<SortOperation> operations = new ArrayList<SortOperation>();
            switch (type) {
                case BUBBLE:
                    bubbleSort(arr, operations);
                    break;
                case SELECTION:
                    selectionSort(arr, operations);
                    break;
                case INSERTION:
                    insertionSort(arr, operations);
                    break;
                case SHELL:
                    shellSort(arr, operations);
                    break;
                case QUICK:
                    quickSort(arr, operations, 0, arr.length - 1);
                    break;
                case MERGE:
                    mergeSort(arr, operations, 0, arr.length - 1, new int[arr.length]);
                    break;
                case HEAP:
                    heapSort(arr, operations);
                    break;
                default:
                    break;
            }
            operations.add(SortOperation.done(type.getDisplayName() + " 完成"));
            return operations;
        }

        private static void bubbleSort(int[] arr, List<SortOperation> operations) {
            int n = arr.length;
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - 1 - i; j++) {
                    operations.add(SortOperation.mark(i, j, j + 1, "冒泡比较：" + j + " 与 " + (j + 1)));
                    if (arr[j] > arr[j + 1]) {
                        swap(arr, j, j + 1);
                        operations.add(SortOperation.swap(j, j + 1, "冒泡交换：" + j + " <-> " + (j + 1)));
                    }
                }
            }
        }

        private static void selectionSort(int[] arr, List<SortOperation> operations) {
            int n = arr.length;
            for (int i = 0; i < n - 1; i++) {
                int minIndex = i;
                for (int j = i + 1; j < n; j++) {
                    operations.add(SortOperation.mark(i, j, minIndex, "选择比较：j=" + j + "，min=" + minIndex));
                    if (arr[j] < arr[minIndex]) {
                        minIndex = j;
                        operations.add(SortOperation.mark(i, j, minIndex, "更新最小值位置：min=" + minIndex));
                    }
                }
                if (minIndex != i) {
                    swap(arr, i, minIndex);
                    operations.add(SortOperation.swap(i, minIndex, "选择交换：" + i + " <-> " + minIndex));
                }
            }
        }

        private static void insertionSort(int[] arr, List<SortOperation> operations) {
            for (int i = 1; i < arr.length; i++) {
                int key = arr[i];
                int j = i - 1;
                operations.add(SortOperation.mark(i, j, -1, "插入：取出 index=" + i));
                while (j >= 0 && arr[j] > key) {
                    arr[j + 1] = arr[j];
                    operations.add(SortOperation.set(j + 1, arr[j], j, "右移：" + j + " -> " + (j + 1)));
                    j--;
                }
                arr[j + 1] = key;
                operations.add(SortOperation.set(j + 1, key, i, "插入：value 放到 " + (j + 1)));
            }
        }

        private static void shellSort(int[] arr, List<SortOperation> operations) {
            int n = arr.length;
            for (int gap = n / 2; gap > 0; gap /= 2) {
                for (int i = gap; i < n; i++) {
                    int temp = arr[i];
                    int j = i;
                    operations.add(SortOperation.mark(i, j, gap, "希尔分组步长 gap=" + gap));
                    while (j >= gap && arr[j - gap] > temp) {
                        arr[j] = arr[j - gap];
                        operations.add(SortOperation.set(j, arr[j - gap], j - gap, "希尔右移：" + (j - gap) + " -> " + j));
                        j -= gap;
                    }
                    arr[j] = temp;
                    operations.add(SortOperation.set(j, temp, i, "希尔插入：" + i + " -> " + j));
                }
            }
        }

        private static void quickSort(int[] arr, List<SortOperation> operations, int low, int high) {
            if (low >= high) {
                return;
            }
            int pivotIndex = partition(arr, operations, low, high);
            quickSort(arr, operations, low, pivotIndex - 1);
            quickSort(arr, operations, pivotIndex + 1, high);
        }

        private static int partition(int[] arr, List<SortOperation> operations, int low, int high) {
            int pivot = arr[high];
            int i = low - 1;
            operations.add(SortOperation.mark(low, high, high, "快速分区：pivot=" + pivot + "@" + high));
            for (int j = low; j < high; j++) {
                operations.add(SortOperation.mark(i, j, high, "快速比较：" + j + " 与 pivot"));
                if (arr[j] <= pivot) {
                    i++;
                    if (i != j) {
                        swap(arr, i, j);
                        operations.add(SortOperation.swap(i, j, "快速交换：" + i + " <-> " + j));
                    }
                }
            }
            if (i + 1 != high) {
                swap(arr, i + 1, high);
                operations.add(SortOperation.swap(i + 1, high, "快速 pivot 归位：" + (i + 1) + " <-> " + high));
            }
            return i + 1;
        }

        private static void mergeSort(int[] arr, List<SortOperation> operations, int left, int right, int[] temp) {
            if (left >= right) {
                return;
            }
            int mid = left + (right - left) / 2;
            mergeSort(arr, operations, left, mid, temp);
            mergeSort(arr, operations, mid + 1, right, temp);
            merge(arr, operations, left, mid, right, temp);
        }

        private static void merge(int[] arr, List<SortOperation> operations, int left, int mid, int right, int[] temp) {
            int i = left;
            int j = mid + 1;
            int t = 0;
            operations.add(SortOperation.mark(left, mid, right, "归并区间：[" + left + "," + right + "]"));

            while (i <= mid && j <= right) {
                if (arr[i] <= arr[j]) {
                    temp[t++] = arr[i++];
                } else {
                    temp[t++] = arr[j++];
                }
            }
            while (i <= mid) {
                temp[t++] = arr[i++];
            }
            while (j <= right) {
                temp[t++] = arr[j++];
            }

            t = 0;
            int write = left;
            while (write <= right) {
                arr[write] = temp[t];
                operations.add(SortOperation.set(write, temp[t], -1, "归并写回：index=" + write));
                write++;
                t++;
            }
        }

        private static void heapSort(int[] arr, List<SortOperation> operations) {
            int n = arr.length;
            for (int i = n / 2 - 1; i >= 0; i--) {
                heapify(arr, operations, n, i);
            }
            for (int i = n - 1; i > 0; i--) {
                swap(arr, 0, i);
                operations.add(SortOperation.swap(0, i, "堆顶交换：0 <-> " + i));
                heapify(arr, operations, i, 0);
            }
        }

        private static void heapify(int[] arr, List<SortOperation> operations, int n, int i) {
            int largest = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;

            if (left < n) {
                operations.add(SortOperation.mark(i, left, largest, "堆比较：" + left + " 与 " + largest));
                if (arr[left] > arr[largest]) {
                    largest = left;
                }
            }
            if (right < n) {
                operations.add(SortOperation.mark(i, right, largest, "堆比较：" + right + " 与 " + largest));
                if (arr[right] > arr[largest]) {
                    largest = right;
                }
            }

            if (largest != i) {
                swap(arr, i, largest);
                operations.add(SortOperation.swap(i, largest, "堆调整交换：" + i + " <-> " + largest));
                heapify(arr, operations, n, largest);
            }
        }

        private static void swap(int[] arr, int i, int j) {
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
    }
}
