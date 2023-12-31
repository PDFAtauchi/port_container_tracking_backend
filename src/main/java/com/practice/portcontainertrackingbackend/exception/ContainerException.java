package com.practice.portcontainertrackingbackend.exception;

public class ContainerException {
    public static class ContainerNotFoundException extends RuntimeException {
        public ContainerNotFoundException(String message) {
            super(message);
        }
    }

    public static class ContainerUpdateException extends RuntimeException {
        public ContainerUpdateException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
