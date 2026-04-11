package com.github.iappapp.panda.common.operation;

import com.github.iappapp.panda.common.enums.OpType;

public class SortOperation {
    public final OpType type;
    public final int a;
    public final int b;
    public final int c;
    public final int value;
    public final int fromIndex;
    public final String message;

    public SortOperation(OpType type, int a, int b, int c, int value, int fromIndex, String message) {
        this.type = type;
        this.a = a;
        this.b = b;
        this.c = c;
        this.value = value;
        this.fromIndex = fromIndex;
        this.message = message;
    }

    public static SortOperation mark(int a, int b, int c, String message) {
        return new SortOperation(OpType.MARK, a, b, c, 0, -1, message);
    }

    public static SortOperation swap(int left, int right, String message) {
        return new SortOperation(OpType.SWAP, left, right, -1, 0, -1, message);
    }

    public static SortOperation set(int index, int value, int fromIndex, String message) {
        return new SortOperation(OpType.SET, index, -1, -1, value, fromIndex, message);
    }

    public static SortOperation done(String message) {
        return new SortOperation(OpType.DONE, -1, -1, -1, 0, -1, message);
    }
}