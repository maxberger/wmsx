package hu.kfki.grid.wmsx.job.description;


public abstract class AbstractJobDescription implements JobDescription {

    public String getStringEntry(final String key, final String defaultValue) {
        final String value = this.getStringEntry(key);
        if (value == null) {
            return defaultValue;
        } else {
            return this.unquote(value);
        }

    }

    private String unquote(final String value) {
        if (value.length() < 2) {
            return value;
        }
        if (value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

}
