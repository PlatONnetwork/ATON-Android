package com.juzix.wallet.component.widget;

import android.text.method.PasswordTransformationMethod;
import android.text.method.ReplacementTransformationMethod;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmountTransformationMethod extends ReplacementTransformationMethod {

    private static char DOT = '\u2022';

    private String originalText;

    public AmountTransformationMethod(String originalText) {
        this.originalText = originalText;
    }

    @Override
    protected char[] getOriginal() {
        return originalText.toCharArray();
    }

    @Override
    protected char[] getReplacement() {
        String regex = "(.*?)([\\d\\,]+\\.\\d+)(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(originalText);
        int size = 0;
        while (matcher.find()) {
            size = matcher.group(2).length();
        }
        return originalText.replaceAll(regex, "$1" + getReplaceDot(size) + "$3").toCharArray();
    }

    private String getReplaceDot(int size) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < size; i++) {
            stringBuilder.append(DOT);
        }

        return stringBuilder.toString();
    }
}
