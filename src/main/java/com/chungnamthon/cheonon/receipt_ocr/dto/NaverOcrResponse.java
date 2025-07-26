package com.chungnamthon.cheonon.receipt_ocr.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class NaverOcrResponse {
    private String version;
    private String requestId;
    private long timestamp;
    private List<Image> images;

    @Getter
    @Setter
    public static class Image {
        private String uid;
        private String name;
        private String inferResult;
        private String message;
        private ValidationResult validationResult;
        private ConvertedImageInfo convertedImageInfo;
        private List<Field> fields;
    }

    @Getter
    @Setter
    public static class ValidationResult {
        private String result;
        private String message;
    }

    @Getter
    @Setter
    public static class ConvertedImageInfo {
        private int width;
        private int height;
        private int pageIndex;
        private boolean longImage;
    }

    @Getter
    @Setter
    public static class Field {
        private String valueType;
        private String inferText;
        private float inferConfidence;
        private String type;
        private boolean lineBreak;
        private BoundingPoly boundingPoly;
    }

    @Getter
    @Setter
    public static class BoundingPoly {
        private List<Vertex> vertices;
    }

    @Getter
    @Setter
    public static class Vertex {
        private float x;
        private float y;
    }
}
