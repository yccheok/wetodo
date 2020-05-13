package com.yocto.wetodo;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yocto.wetodo.color.ColorPickerDialogFragment;
import com.yocto.wetodo.font.FontType;
import com.yocto.wetodo.model.TodoFolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.yocto.wetodo.Constants.MOST_RECENT_COLOR_LIST_SIZE;
import static com.yocto.wetodo.Constants.PREFERRED_THEME;

public enum WeTodoOptions {
    INSTANCE;

    private static final String TAG = "WeTodoOptions";

    private static final String SYNC_REQUIRED = "SYNC_REQUIRED";
    private static final String SELECTED_TODO_FOLDER_INDEX = "SELECTED_TODO_FOLDER_INDEX";

    public static final String THEME = "THEME";
    public static final String FONT_TYPE = "FONT_TYPE";
    private static final String SELECTED_COLOR_PICKER_DIALOG_PAGE_INDICES = "SELECTED_COLOR_PICKER_DIALOG_PAGE_INDICES";
    private static final String MOST_RECENT_SELECTED_COLOR_LISTS = "MOST_RECENT_SELECTED_COLOR_LISTS";

    @NonNull
    private Theme theme = PREFERRED_THEME;

    private FontType fontType = FontType.SlabSerif;

    private Map<ColorPickerDialogFragment.Type, Integer> selectedColorPickerDialogPageIndices = new EnumMap<>(ColorPickerDialogFragment.Type.class);
    private Map<ColorPickerDialogFragment.Type, List<Integer>> mostRecentSelectedColorLists = new EnumMap<>(ColorPickerDialogFragment.Type.class);

    // We will not save this value to disk.
    private transient TodoFolder selectedTodoFolder = null;

    WeTodoOptions() {
        final Gson gson = getGsonForRead();
        final SharedPreferences sharedPreferences = WeTodoApplication.instance().getSharedPreferences();

        try {
            String json_theme = sharedPreferences.getString(THEME, null);
            String json_selected_color_picker_dialog_page_indices = sharedPreferences.getString(SELECTED_COLOR_PICKER_DIALOG_PAGE_INDICES, null);
            String json_most_recent_selected_color_lists = sharedPreferences.getString(MOST_RECENT_SELECTED_COLOR_LISTS, null);
            String json_font_type = sharedPreferences.getString(FONT_TYPE, null);

            if (!Utils.isNullOrEmpty(json_theme)) {
                Theme theme = gson.fromJson(json_theme, Theme.class);
                if (theme != null) {
                    this.theme = theme;
                }
            }

            if (!Utils.isNullOrEmpty(json_selected_color_picker_dialog_page_indices)) {
                EnumMap<ColorPickerDialogFragment.Type, Integer> selectedColorPickerDialogPageIndices = gson.fromJson(
                        json_selected_color_picker_dialog_page_indices,
                        new TypeToken<EnumMap<ColorPickerDialogFragment.Type, Integer>>(){}.getType()
                );

                if (selectedColorPickerDialogPageIndices != null) {
                    this.selectedColorPickerDialogPageIndices = selectedColorPickerDialogPageIndices;
                }
            }

            if (!Utils.isNullOrEmpty(json_most_recent_selected_color_lists)) {
                EnumMap<ColorPickerDialogFragment.Type, List<Integer>> mostRecentSelectedColorLists = gson.fromJson(
                        json_most_recent_selected_color_lists,
                        new TypeToken<EnumMap<ColorPickerDialogFragment.Type, List<Integer>>>(){}.getType()
                );

                if (mostRecentSelectedColorLists != null) {
                    this.mostRecentSelectedColorLists = mostRecentSelectedColorLists;
                }
            }

            if (!Utils.isNullOrEmpty(json_font_type)) {
                FontType fontType = gson.fromJson(json_font_type, FontType.class);
                if (fontType != null) {
                    this.fontType = fontType;
                }
            }
        } catch (Exception | AssertionError e) {
            Log.e(TAG, "", e);
            Utils.trackEvent(TAG, "fatal", e.getMessage());
        }
    }

    public boolean saveToSharedPreferences() {
        final SharedPreferences sharedPreferences = WeTodoApplication.instance().getSharedPreferences();
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final Gson gson = getGsonForWrite();

        try {
            String json_theme = gson.toJson(theme);
            String json_selected_color_picker_dialog_page_indices = gson.toJson(selectedColorPickerDialogPageIndices);
            String json_most_recent_selected_color_lists = gson.toJson(mostRecentSelectedColorLists);
            String json_font_type = gson.toJson(fontType);

            editor.putString(THEME, json_theme);
            editor.putString(SELECTED_COLOR_PICKER_DIALOG_PAGE_INDICES, json_selected_color_picker_dialog_page_indices);
            editor.putString(MOST_RECENT_SELECTED_COLOR_LISTS, json_most_recent_selected_color_lists);
            editor.putString(FONT_TYPE, json_font_type);

            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "", e);
            Utils.trackEvent("saveToSharedPreferences", "fatal", e.getMessage());

            return false;
        }

        return true;
    }

    public static int getSelectedTodoFolderIndex() {
        return WeTodoApplication.instance().getSharedPreferences().getInt(SELECTED_TODO_FOLDER_INDEX, 0);
    }

    public static void setSelectedTodoFolderIndex(int selectedTodoFolderIndex) {
        WeTodoApplication.instance().getSharedPreferences().edit().putInt(SELECTED_TODO_FOLDER_INDEX, selectedTodoFolderIndex).apply();
    }

    public static void setSyncRequired(boolean syncRequired) {
        WeTodoApplication.instance().getSharedPreferences().edit().putBoolean(SYNC_REQUIRED, syncRequired).apply();
    }

    public Theme getTheme() {
        return this.theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    private Gson getGsonForWrite() {
        GsonBuilder builder = new GsonBuilder();
        //builder.enableComplexMapKeySerialization();
        final Gson gson = builder.create();
        return gson;
    }

    private static Gson getGsonForRead() {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        new TypeToken<EnumMap<ColorPickerDialogFragment.Type, Integer>>() {}.getType(),
                        new EnumMapInstanceCreator<ColorPickerDialogFragment.Type, Integer>(ColorPickerDialogFragment.Type.class)
                )
                .registerTypeAdapter(
                        new TypeToken<EnumMap<ColorPickerDialogFragment.Type, List<Integer>>>() {}.getType(),
                        new EnumMapInstanceCreator<ColorPickerDialogFragment.Type, List<Integer>>(ColorPickerDialogFragment.Type.class)
                )
                .create();
        return gson;
    }

    public void setSelectedColorPickerDialogPageIndex(ColorPickerDialogFragment.Type type, int index) {
        this.selectedColorPickerDialogPageIndices.put(type, index);
    }

    public int getSelectedColorPickerDialogPageIndex(ColorPickerDialogFragment.Type type) {
        Integer index = this.selectedColorPickerDialogPageIndices.get(type);
        if (index == null) {
            return 0;
        }
        return index;
    }

    public List<Integer> getMostRecentSelectedColorLists(ColorPickerDialogFragment.Type type) {
        List<Integer> colors = mostRecentSelectedColorLists.get(type);
        if (colors == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(
                new ArrayList<>(colors)
        );
    }

    public void setAndClearMostRecentSelectedColorList(ColorPickerDialogFragment.Type type, int color) {
        List<Integer> colors = this.mostRecentSelectedColorLists.get(type);
        if (colors == null) {
            colors = new ArrayList<>();
            colors.add(color);
            this.mostRecentSelectedColorLists.put(type, colors);
        } else {
            if (false == colors.contains(color)) {
                colors.add(0, color);
                if (colors.size() > MOST_RECENT_COLOR_LIST_SIZE) {
                    colors = colors.subList(0, MOST_RECENT_COLOR_LIST_SIZE);
                    this.mostRecentSelectedColorLists.put(type, colors);
                }
            }
        }
    }

    public FontType getFontType() {
        return this.fontType;
    }

    public void setFontType(FontType fontType) {
        this.fontType = fontType;
    }

    public TodoFolder getSelectedTodoFolder() {
        return this.selectedTodoFolder;
    }

    public void setSelectedTodoFolder(TodoFolder selectedTodoFolder) {
        this.selectedTodoFolder = selectedTodoFolder;
    }
}
