package com.lezenford.netty.advanced.common.message;

public class FileContentMessage extends Message {
    private long startPosition;
    private boolean last;
    private byte[] content;

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isLast() {
        return last;
    }



    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public long getStartPosition() {
        return startPosition;
    }
}
