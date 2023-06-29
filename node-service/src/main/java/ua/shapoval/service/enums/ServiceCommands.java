package ua.shapoval.service.enums;

public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
    ServiceCommands(String value) {
        this.value = value;
    }

    public static ServiceCommands fromValues(String value){
        for (ServiceCommands serviceCommands : ServiceCommands.values()){
            if (serviceCommands.equals(value)){
                return serviceCommands;
            }
        }
        return null;
    }
}
