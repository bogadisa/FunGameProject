package secondEngine.renderer;

public abstract class BatchRendererInterface {
    protected int numObjects;

    protected boolean hasRoom;

    protected float[] vertices;

    protected int vaoID, vboID;
    protected int maxBatchSize;
    protected Shader shader;

    public BatchRendererInterface(int maxBatchSize, Shader shader) {
        this.shader = shader;
        this.maxBatchSize = maxBatchSize;

        this.numObjects = 0;
    }

    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3, 2, 0, 0, 2, 1 7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }
}
