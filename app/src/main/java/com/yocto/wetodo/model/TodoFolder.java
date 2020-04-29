package com.yocto.wetodo.model;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.TypedValue;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.yocto.wetodo.R;
import com.yocto.wetodo.ThemeType;
import com.yocto.wetodo.Utils;
import com.yocto.wetodo.WeTodoApplication;

import java.util.ArrayList;
import java.util.List;

import static com.yocto.wetodo.ui.Utils.isCustomColorIndex;

@Entity(tableName = "todo_folder")
public class TodoFolder implements Parcelable {
    public static final String INBOX_UUID = "42a5ba96-4f3c-4162-9d48-bca95fdb5129";
    public static final String HOME_UUID = "e7d2b0b2-fe7e-45e7-b2be-1b7045e858b2";
    public static final String WORK_UUID = "c4b4e142-e988-4de2-8b36-9bdeb35fde4b";
    public static final String SETTINGS_UUID = "9b65bea9-0512-422c-a2ed-6b256c58738f";

    public enum Type implements Parcelable {
        Inbox(1),
        Custom(2),
        Settings(3);

        public final int code;

        Type(int code) {
            this.code = code;
        }

        public static final Parcelable.Creator<Type> CREATOR = new Parcelable.Creator<Type>() {
            public Type createFromParcel(Parcel in) {
                return Type.valueOf(in.readString());
            }

            public Type[] newArray(int size) {
                return new Type[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeString(this.name());
        }
    };

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "type")
    @TypeConverters(TodoFolderTypeConverter.class)
    private final Type type;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "color_index")
    private int colorIndex;

    @ColumnInfo(name = "custom_color")
    private int customColor;

    @ColumnInfo(name = "icon_index")
    public final int iconIndex;

    @ColumnInfo(name = "order")
    private int order;

    @Ignore
    private transient boolean focused;

    @ColumnInfo(name = "synced_timestamp")
    private long syncedTimestamp;

    @ColumnInfo(name = "uuid")
    private final String uuid;

    private static final int[] colorAttrs = {
            R.attr.blueTabColor,
            R.attr.greenTabColor,
            R.attr.redTabColor,
            R.attr.orangeTabColor,
            R.attr.purpleTabColor,
            R.attr.yellowTabColor,
            R.attr.cyanTabColor,
            R.attr.greyTabColor,
    };

    private static final int[] colorStringResourceIds = {
            R.string.blue,
            R.string.green,
            R.string.red,
            R.string.orange,
            R.string.purple,
            R.string.yellow,
            R.string.cyan,
            R.string.grey
    };

    private static final int[] iconAttrs = {
            R.attr.settingsTabIcon
    };

    protected TodoFolder(Parcel in) {
        id = in.readLong();
        type = in.readParcelable(Type.class.getClassLoader());
        name = in.readString();
        colorIndex = in.readInt();
        customColor = in.readInt();
        iconIndex = in.readInt();
        order = in.readInt();
        focused = in.readByte() != 0;
        syncedTimestamp = in.readLong();
        uuid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(type, flags);
        dest.writeString(name);
        dest.writeInt(colorIndex);
        dest.writeInt(customColor);
        dest.writeInt(iconIndex);
        dest.writeInt(order);
        dest.writeByte((byte) (focused ? 1 : 0));
        dest.writeLong(syncedTimestamp);
        dest.writeString(uuid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TodoFolder> CREATOR = new Creator<TodoFolder>() {
        @Override
        public TodoFolder createFromParcel(Parcel in) {
            return new TodoFolder(in);
        }

        @Override
        public TodoFolder[] newArray(int size) {
            return new TodoFolder[size];
        }
    };

    public static int getColorsAttrsLength() {
        return colorAttrs.length;
    }

    @Ignore
    private TodoFolder(Type type, String name, int colorIndex, int customColor, int iconIndex) {
        this(type, name, colorIndex, customColor, iconIndex, Utils.generateUUID());
    }

    public TodoFolder(Type type, String name, int colorIndex, int customColor, int iconIndex, String uuid) {
        this.type = type;
        this.name = name;
        this.colorIndex = colorIndex;
        this.customColor = customColor;
        this.iconIndex = iconIndex;
        this.uuid = uuid;
    }

    // Don't forget to revise copyWithoutIdAndUuid function in com.yocto.wetodo.repository.Utils.
    public TodoFolder copy() {
        TodoFolder projectInfo = new TodoFolder(this.type, this.name, this.colorIndex, this.customColor, this.iconIndex, this.uuid);
        projectInfo.id = this.id;
        projectInfo.order = this.order;
        projectInfo.focused = this.focused;
        projectInfo.syncedTimestamp = this.syncedTimestamp;
        return projectInfo;
    }

    public static List<TodoFolder> copy(List<TodoFolder> todoFolders) {
        List<TodoFolder> result = new ArrayList<>();
        for (TodoFolder todoFolder : todoFolders) {
            result.add(todoFolder.copy());
        }
        return result;
    }

    public static TodoFolder newInstance(Type type, String name, int colorIndex, int customColor, int iconIndex) {
        return new TodoFolder(type, name, colorIndex, customColor, iconIndex);
    }

    public static TodoFolder newInstance(Type type, String name, int colorIndex, int customColor, int iconIndex, String uuid) {
        return new TodoFolder(type, name, colorIndex, customColor, iconIndex, uuid);
    }

    public static int[] getColorStringResourceIds() {
        int[] copy = new int[colorStringResourceIds.length];
        System.arraycopy(colorStringResourceIds, 0, copy, 0, colorStringResourceIds.length);
        return copy;
    }

    public static int[] getColors() {
        int[] colors = new int[colorAttrs.length];
        Context context = new ContextThemeWrapper(
                WeTodoApplication.instance(),
                com.yocto.wetodo.ui.Utils.getThemeResourceId(ThemeType.Main)
        );
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        for (int i=0, ei=colorAttrs.length; i<ei; i++) {
            theme.resolveAttribute(colorAttrs[i], typedValue, true);
            colors[i] = typedValue.data;
        }
        return colors;
    }

    public int getColor() {
        if (isCustomColorIndex(colorIndex)) {
            return customColor;
        } else {
            Context context = new ContextThemeWrapper(
                    WeTodoApplication.instance(),
                    com.yocto.wetodo.ui.Utils.getThemeResourceId(ThemeType.Main)
            );
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(colorAttrs[colorIndex % colorAttrs.length], typedValue, true);
            return typedValue.data;
        }
    }

    public int getIconResourceId() {
        if (iconIndex < 0) {
            return 0;
        }

        Context context = new ContextThemeWrapper(
                WeTodoApplication.instance(),
                com.yocto.wetodo.ui.Utils.getThemeResourceId(ThemeType.Main)
        );
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(iconAttrs[iconIndex%iconAttrs.length], typedValue, true);
        return typedValue.resourceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public int getCustomColor() {
        return customColor;
    }

    public void setCustomColor(int customColor) {
        this.customColor = customColor;
    }

    public int getIconIndex() {
        return iconIndex;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public long getSyncedTimestamp() {
        return syncedTimestamp;
    }

    public void setSyncedTimestamp(long syncedTimestamp) {
        this.syncedTimestamp = syncedTimestamp;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TodoFolder projectInfo = (TodoFolder) o;

        if (id != projectInfo.id) return false;
        if (colorIndex != projectInfo.colorIndex) return false;
        if (customColor != projectInfo.customColor) return false;
        if (iconIndex != projectInfo.iconIndex) return false;
        if (order != projectInfo.order) return false;
        if (focused != projectInfo.focused) return false;
        if (syncedTimestamp != projectInfo.syncedTimestamp) return false;
        if (type != projectInfo.type) return false;
        if (name != null ? !name.equals(projectInfo.name) : projectInfo.name != null) return false;
        return uuid != null ? uuid.equals(projectInfo.uuid) : projectInfo.uuid == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + colorIndex;
        result = 31 * result + customColor;
        result = 31 * result + iconIndex;
        result = 31 * result + order;
        result = 31 * result + (focused ? 1 : 0);
        result = 31 * result + (int) (syncedTimestamp ^ (syncedTimestamp >>> 32));
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }

    public boolean isImmutableType() {
        return this.type == Type.Inbox || this.type == Type.Settings;
    }

    public boolean hasImmutableUuid() {
        return hasInboxUuid() || hasSettingsUuid();
    }

    public boolean hasInboxUuid() {
        return INBOX_UUID.equals(this.uuid);
    }

    public boolean hasSettingsUuid() {
        return SETTINGS_UUID.equals(this.uuid);
    }

    public boolean hasHomeUuid() {
        return HOME_UUID.equals(this.uuid);
    }

    public boolean hasWorkUuid() {
        return WORK_UUID.equals(this.uuid);
    }

    public boolean isOriginalInboxFolder() {
        return type == Type.Inbox && hasInboxUuid();
    }

    public boolean isOriginalSettingsFolder() {
        return type == Type.Settings && hasSettingsUuid();
    }

    public boolean isOriginalHomeFolder() {
        return type == Type.Custom && hasHomeUuid();
    }

    public boolean isOriginalWorkFolder() {
        return type == Type.Custom && hasWorkUuid();
    }

    public static int getColorAttr(int index) {
        return colorAttrs[index];
    }
}

