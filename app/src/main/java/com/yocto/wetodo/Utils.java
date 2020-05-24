package com.yocto.wetodo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.yocto.wetodo.model.TodoFolder;
import com.yocto.wetodo.repository.TodoFolderRepository;
import com.yocto.wetodo.repository.UpdateOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.yocto.wetodo.repository.Utils.getExecutorForRepository;

public class Utils {
    public static class TypefaceLazyHolder {
        public static final Typeface NOTO_SANS_SEMI_BOLD_TYPE_FACE = createFromAsset("fonts/NotoSans-SemiBold.ttf");
        public static final Typeface NOTO_SANS_REGULAR_TYPE_FACE = createFromAsset("fonts/NotoSans-Regular.ttf");

        public static final Typeface ROBOTO_CONDENSED_REGULAR_TYPE_FACE = createFromAsset("fonts/RobotoCondensed-Regular.ttf");
        public static final Typeface ROBOTO_CONDENSED_BOLD_TYPE_FACE = createFromAsset("fonts/RobotoCondensed-Bold.ttf");
        public static final Typeface ROBOTO_SLAB_REGULAR_TYPE_FACE = createFromAsset("fonts/RobotoSlab-Regular.ttf");

        public static final Typeface ROBOTO_REGULAR_TYPE_FACE = createFromAsset("fonts/Roboto-Regular.ttf");
        public static final Typeface ROBOTO_MEDIUM_TYPE_FACE = createFromAsset("fonts/Roboto-Medium.ttf");
        public static final Typeface ROBOTO_ITALIC_TYPE_FACE = createFromAsset("fonts/Roboto-Italic.ttf");
        public static final Typeface ROBOTO_BOLD_TYPE_FACE = createFromAsset("fonts/Roboto-Bold.ttf");
        public static final Typeface ROBOTO_LIGHT_TYPE_FACE = createFromAsset("fonts/Roboto-Light.ttf");

        public static final Typeface ROBOTO_MONO_REGULAR_TYPE_FACE = createFromAsset("fonts/RobotoMono-Regular.ttf");

        // Can differentiate between 0 and O.
        public static final Typeface CONSOLAS = createFromAsset("fonts/Consolas.ttf");
    }

    public interface Callable<T> {
        void call(T t);
    }

    private Utils() {
    }

    public static void trackEvent(String category, String action, String label) {
    }

    private static Typeface createFromAsset(String path) {
        WeTodoApplication weTodoApplication = WeTodoApplication.instance();

        if (weTodoApplication == null) {
            trackEvent("createFromAsset", "fatal", path);
            return null;
        }

        return Typeface.createFromAsset(weTodoApplication.getAssets(), path);
    }

    public static String getPageTitle(TodoFolder todoFolder) {
        switch (todoFolder.getType()) {
            case Inbox:
                return WeTodoApplication.instance().getString(R.string.inbox);
            case Custom:
                return todoFolder.getName();
            case Settings:
                return null;
            default:
                Assert(false);
                return null;
        }
    }

    public static float spToPixelInFloat(float sp) {
        DisplayMetrics displayMetrics = WeTodoApplication.instance().getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, displayMetrics);
    }

    public static int dpToPixel(float dp) {
        DisplayMetrics displayMetrics = WeTodoApplication.instance().getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics) + 0.5);
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static void Assert(boolean condition) {
        if (!condition) {
            throw new java.lang.RuntimeException();
        }
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static void ensureToolbarTextViewBestTextSize(final TextView toolbarTextView, final float originalToolbarTextViewTextSize) {

        if (toolbarTextView != null && originalToolbarTextViewTextSize > 0) {
            final ViewTreeObserver viewTreeObserver = toolbarTextView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @SuppressLint("NewApi")
                @Override
                public void onGlobalLayout() {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        toolbarTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        toolbarTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }

                    // Sound strange at the first place. But, this is a hack to make getLineCount
                    // more accurate.
                    final ViewTreeObserver viewTreeObserver = toolbarTextView.getViewTreeObserver();
                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onGlobalLayout() {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                toolbarTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            } else {
                                toolbarTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            }

                            final int lineCount = toolbarTextView.getLineCount();

                            if (lineCount > 1) {
                                float minPixel = Utils.spToPixelInFloat(14);
                                toolbarTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.max(minPixel, originalToolbarTextViewTextSize * 3.0f / 5.0f));
                            } else if (lineCount == 1) {
                                toolbarTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, originalToolbarTextViewTextSize);
                            }
                        }
                    });
                }
            });
        }
    }

    public static void sortTodoFoldersBasedOnOriginal(List<TodoFolder> todoFolders) {
        Collections.sort(todoFolders, (t0, t1) -> {
            if (t0.isOriginalSettingsFolder()) {
                return +1;
            }

            if (t1.isOriginalSettingsFolder()) {
                return -1;
            }

            return 0;
        });
    }

    public static boolean ensureTodoFoldersAreValid(List<TodoFolder> todoFolders, boolean async) {
        final List<TodoFolder> mutableTodoFolders = TodoFolder.copy(todoFolders);

        final List<TodoFolder> invalidTodoFolders = new ArrayList<>();

        ////////////////////////////////////////////////////////////////////////////////////////////
        // FIRST TIME
        ////////////////////////////////////////////////////////////////////////////////////////////
        if (todoFolders.isEmpty()) {
            mutableTodoFolders.add(TodoFolder.newInstance(
                    TodoFolder.Type.Custom,
                    WeTodoApplication.instance().getString(R.string.home),
                    2,
                    0,
                    -1,
                    TodoFolder.HOME_UUID
            ));

            mutableTodoFolders.add(TodoFolder.newInstance(
                    TodoFolder.Type.Custom,
                    WeTodoApplication.instance().getString(R.string.work),
                    3,
                    0,
                    -1,
                    TodoFolder.WORK_UUID
            ));
        }

        boolean hasOriginalInboxFolder = false;
        boolean hasOriginalSettingsFolder = false;

        boolean hasOriginalHomeFolder = false;
        boolean hasOriginalWorkFolder = false;

        for (TodoFolder todoFolder : mutableTodoFolders) {
            if (todoFolder.isOriginalInboxFolder()) {
                if (false == hasOriginalInboxFolder) {
                    hasOriginalInboxFolder = true;
                } else {
                    invalidTodoFolders.add(todoFolder);
                }
                continue;
            } else if (todoFolder.getType() == TodoFolder.Type.Inbox) {
                invalidTodoFolders.add(todoFolder);
                continue;
            } else if (todoFolder.hasInboxUuid()) {
                invalidTodoFolders.add(todoFolder);
                continue;
            }


            if (todoFolder.isOriginalSettingsFolder()) {
                if (false == hasOriginalSettingsFolder) {
                    hasOriginalSettingsFolder = true;
                } else {
                    invalidTodoFolders.add(todoFolder);
                }
                continue;
            } else if (todoFolder.getType() == TodoFolder.Type.Settings) {
                invalidTodoFolders.add(todoFolder);
                continue;
            } else if (todoFolder.hasSettingsUuid()) {
                invalidTodoFolders.add(todoFolder);
                continue;
            }

            // Avoid duplication.
            if (todoFolder.isOriginalHomeFolder()) {
                if (false == hasOriginalHomeFolder) {
                    hasOriginalHomeFolder = true;
                } else {
                    invalidTodoFolders.add(todoFolder);
                }
                continue;
            }

            // Avoid duplication.
            if (todoFolder.isOriginalWorkFolder()) {
                if (false == hasOriginalWorkFolder) {
                    hasOriginalWorkFolder = true;
                } else {
                    invalidTodoFolders.add(todoFolder);
                }
                continue;
            }
        }

        mutableTodoFolders.removeAll(invalidTodoFolders);

        if (!hasOriginalInboxFolder) {
            mutableTodoFolders.add(0, TodoFolder.newInstance(
                    TodoFolder.Type.Inbox,
                    null,
                    0,
                    0,
                    -1,
                    TodoFolder.INBOX_UUID
            ));
        }

        if (!hasOriginalSettingsFolder) {
            mutableTodoFolders.add(TodoFolder.newInstance(
                    TodoFolder.Type.Settings,
                    null,
                    0,
                    0,
                    0,
                    TodoFolder.SETTINGS_UUID
            ));
        }

        sortTodoFoldersBasedOnOriginal(mutableTodoFolders);

        for (int i = 0, ei = mutableTodoFolders.size(); i < ei; i++) {
            TodoFolder mutableTodoFolder = mutableTodoFolders.get(i);
            mutableTodoFolder.setOrder(i);
        }

        if (todoFolders.equals(mutableTodoFolders) && invalidTodoFolders.isEmpty()) {
            return true;
        }

        // Make sure all modifications will be synced to cloud.
        final long syncedTimestamp = System.currentTimeMillis();

        for (int i = 0, ei = mutableTodoFolders.size(); i < ei; i++) {
            TodoFolder mutableTodoFolder = mutableTodoFolders.get(i);
            mutableTodoFolder.setSyncedTimestamp(syncedTimestamp);
        }

        if (async) {
            getExecutorForRepository().execute(() -> {
                fixTodoFolders(mutableTodoFolders, invalidTodoFolders);
                WeTodoOptions.setSyncRequired(true);
            });
        } else {
            fixTodoFolders(mutableTodoFolders, invalidTodoFolders);
            WeTodoOptions.setSyncRequired(true);
        }

        return false;
    }

    public static boolean isValidId(long id) {
        return id > 0;
    }

    public static List<UpdateOrder> updateTodoFolderOrder(List<TodoFolder> todoFolders) {
        List<UpdateOrder> updateOrders = new ArrayList<>();

        for (int i = 0, ei = todoFolders.size(); i < ei; i++) {
            TodoFolder todoFolder = todoFolders.get(i);

            int originalOrder = todoFolder.getOrder();
            todoFolder.setOrder(i);

            if (originalOrder != todoFolder.getOrder() && Utils.isValidId(todoFolder.getId())) {
                updateOrders.add(UpdateOrder.newInstace(
                        todoFolder.getId(),
                        todoFolder.getOrder()
                ));
            }
        }

        return updateOrders;
    }

    private static void fixTodoFolders(List<TodoFolder> mutableTodoFolders, List<TodoFolder> invalidTodoFolders) {
        for (TodoFolder invalidTodoFolder : invalidTodoFolders) {
            if (invalidTodoFolder.hasImmutableUuid()) {
                TodoFolderRepository.INSTANCE.delete(invalidTodoFolder);
            } else {
                TodoFolderRepository.INSTANCE.permanentDelete(invalidTodoFolder);
            }
        }

        TodoFolderRepository.INSTANCE.insert(mutableTodoFolders);
    }

    public static float dpToPixelInFloat(float dp) {
        DisplayMetrics displayMetrics = WeTodoApplication.instance().getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    public static <T> void ready(LiveData<T> liveData, LifecycleOwner lifecycleOwner, Callable<T> callable) {
        T t = liveData.getValue();
        if (t != null) {
            callable.call(t);
            return;
        }

        liveData.observe(lifecycleOwner, new Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                liveData.removeObserver(this);
                callable.call(t);
            }
        });
    }

    public static void setCustomTypeFace(View view, Typeface typeFace) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(typeFace);
        } else if (view instanceof EditText) {
            ((EditText) view).setTypeface(typeFace);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                setCustomTypeFace(viewGroup.getChildAt(i), typeFace);
            }
        }
    }

    public static boolean equals(String a, String b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static Set<Integer> toSet(int[] values) {
        final Set<Integer> set = new HashSet<>();
        for (int value : values) {
            set.add(value);
        }
        return set;
    }

    public static int[] toArray(List<Integer> values) {
        final int size = values.size();
        int[] array = new int[size];
        for (int i=0; i<size; i++) {
            array[i] = values.get(i);
        }
        return array;
    }

    public static void placeCursorAtEndOfText(EditText editText) {
        editText.setSelection(editText.getText().length());
    }

    public static boolean weTodoApplicationIsNull() {
        return WeTodoApplication.instance() == null;
    }

    private static View getRootView(Activity activity) {
        return activity.getWindow().getDecorView().findViewById(android.R.id.content);
    }

    public static void hideKeyboard(Activity activity) {
        hideKeyboard(getRootView(activity));
    }

    public static void hideKeyboard(Fragment fragment) {
        hideKeyboard(fragment.getView());
    }

    public static void hideKeyboard(View view) {
        if (view == null) {
            return;
        }

        final View rootView = view.getRootView();

        if (rootView == null) {
            return;
        }

        final Object systemService = view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (systemService instanceof InputMethodManager) {
            InputMethodManager imm = (InputMethodManager) systemService;
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }
    }

    public static void focusAndShowKeyboard(Context context, EditText editText) {
        editText.requestFocus();

        if (context == null) {
            context = WeTodoApplication.instance();

            if (context == null) {
                trackEvent("focusAndShowKeyboard", "fatal", null);
                return;
            }
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }
}
