package com.stacit.stac.activities.StacAI;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Translation
{
    private String tag;
    private Boolean modelDownloaded = false;
    private Boolean languageIDSuccess = false;
    private Boolean translationSuccess = false;
    private String setLanguage = "EN";
    private String translatedText;
    private Translator stacTranslator;


    private ArrayList<ModelLanguages> languageArrayList;

    public Translation()
    {
        loadAvailableLanguages();
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.FRENCH)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build();
        stacTranslator = com.google.mlkit.nl.translate.Translation.getClient(options);
    }

    private void loadAvailableLanguages()
    {
        languageArrayList = new ArrayList<>();

        List<String> languageCodeList = TranslateLanguage.getAllLanguages();
        for (String languageCode: languageCodeList)
        {
            String languageTitle = new Locale(languageCode).getDisplayLanguage();
            Log.d(TAG, "loadAvailableLanguages: languageCode: "+languageCode );
            Log.d(TAG, "loadAvailableLanguages: languageTitle: "+languageTitle);

            ModelLanguages modelLanguages = new ModelLanguages(languageCode, languageTitle);
            languageArrayList.add(modelLanguages);
        }
    }

    //gets the language tag of the text to be translated
    public void languageID(String text)
    {
        LanguageIdentifier languageIdentifier =
                LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(text)
                .addOnSuccessListener(new OnSuccessListener<String>()
                {
                    @Override
                    public void onSuccess(String languageCode)
                    {
                        if (languageCode.equals("und"))
                        {
                            languageIDSuccess = false;
                        }
                        else
                        {
                            languageIDSuccess = true;
                            tag = languageCode;
                        }
                    }
                });
    }
    //downloads the language pack if needed
    private void downloadLanguage()
    {
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        stacTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void unused)
                    {
                        modelDownloaded = true;
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        modelDownloaded = false;
                    }
                });
    }
    //translate the text after the language was detected and language model was downloaded
    private String translateText(String text)
    {
        if (modelDownloaded)
        {
            stacTranslator.translate(text)
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            translationSuccess = true;
                            translatedText = s;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            translationSuccess = false;
                        }
                    });
        }
        //check to see if the translation was successful
        if (!translationSuccess)
        {
            translatedText = text;
        }
        return translatedText;
    }
    //put everything into a single public function
    public String stacTranslate(String text)
    {
        String textHolder;
        languageID(text);
        if (languageIDSuccess)
        {
            downloadLanguage();
            textHolder = translateText(text);
        }
        else
        {
            textHolder = text;
        }
        return textHolder;
    }
}