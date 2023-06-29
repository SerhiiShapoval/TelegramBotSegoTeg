package ua.shapoval.service.enums;

public enum LinkType {
    GET_DOC("/api/file/get-doc"),
    GET_PHOTO("/api/file/get-photo");

    private final String link;

    LinkType(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return link;
    }

}
