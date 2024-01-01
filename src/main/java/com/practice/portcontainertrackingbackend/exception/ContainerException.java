package com.practice.portcontainertrackingbackend.exception;

public class ContainerException {
    public static class ContainerNotFoundException extends RuntimeException {
        public ContainerNotFoundException(String message) {
            super(message);
        }
    }
}
