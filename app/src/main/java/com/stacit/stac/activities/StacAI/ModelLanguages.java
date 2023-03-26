package com.stacit.stac.activities.StacAI;

public class ModelLanguages
{
    String languageCode;
    String languageTitle;

    public ModelLanguages(String languageCode, String languageTitle)
    {
        this.languageCode = languageCode;
        this.languageTitle = languageTitle;
    }

    //getters and setters
    public String getLanguageCode()
    {
        return languageCode;
    }

    public void setLanguageCode(String languageCode)
    {
        this.languageCode = languageCode;
    }

    public String getLanguageTitle() {
        return languageTitle;
    }

    public void setLanguageTitle(String languageTitle) {
        this.languageTitle = languageTitle;
    }
}
