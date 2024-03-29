package com.kverchi.diary.service.email.impl;


public enum EmailTemplate {
    REGISTRATION_EMAIL("registrationMailTemplate.html", "confirm.registration"),
    RESET_PASSWORD_EMAIL("resetPasswordMailTemplate.html", "resetting.password");

    private final String template;
    private final String subjectLocalizationKey;

    EmailTemplate(final String template, final String localizationKey) {

        this.template = template;
        this.subjectLocalizationKey = localizationKey;
    }
    public String getTemplate() {
        return this.template;
    }
    public String getSubjectLocalizationKey() {
        return this.subjectLocalizationKey;
    }

    @Override
    public String toString() {
        return template;
    }
}
