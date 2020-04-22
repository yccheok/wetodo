package com.yocto.wetodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.yocto.wetodo.theme.ThemeIcon;

import static com.yocto.wetodo.Utils.Assert;

public enum Theme implements Parcelable {
    Brown(0, R.color.colorPrimaryLight, R.color.colorPrimaryLight, R.color.normalTabColorLight, R.string.roman_coffee, ThemeIcon.None, false),
    Black(1, R.color.colorPrimaryBlack, R.color.colorPrimaryBlack, R.color.normalTabColorDark, R.string.mine_shaft, ThemeIcon.None, false),
    Pink(2, R.color.colorPrimaryPink, R.color.colorPrimaryDarkPink, R.color.normalTabColorPink, R.string.french_rose, ThemeIcon.White, false),
    PinkBlack(3, R.color.colorPrimaryPink, R.color.colorPrimaryDarkPink, R.color.normalTabColorPink, R.string.french_rose, ThemeIcon.Black, false),
    Dark(4, R.color.colorPrimaryBlack, R.color.colorPrimaryBlack, R.color.normalTabColorDark, R.string.dark, ThemeIcon.None, false),
    White(5, R.color.colorPrimaryPureWhite, R.color.primaryTextColorLight, R.color.normalTabColorPureWhite, R.string.cotton, ThemeIcon.None, true),
    Purple(6, R.color.colorPrimaryPurple, R.color.colorPrimaryDarkPurple, R.color.normalTabColorPurple, R.string.lavender, ThemeIcon.White, true),
    PurpleBlack(7, R.color.colorPrimaryPurple, R.color.colorPrimaryDarkPurple, R.color.normalTabColorPurple, R.string.lavender, ThemeIcon.Black, true),
    Yellow(8, R.color.colorPrimaryYellow, R.color.colorPrimaryDarkYellow, R.color.normalTabColorYellow, R.string.lemon, ThemeIcon.White, true),
    YellowBlack(9, R.color.colorPrimaryYellow, R.color.colorPrimaryDarkYellow, R.color.normalTabColorYellow, R.string.lemon, ThemeIcon.Black, true),
    Red(10, R.color.colorPrimaryRed, R.color.colorPrimaryRed, R.color.normalTabColorRed, R.string.strawberry, ThemeIcon.None, true),
    Blue(11, R.color.colorPrimaryBlue, R.color.colorPrimaryBlue, R.color.normalTabColorBlue, R.string.azure, ThemeIcon.None, true),
    Green(12, R.color.colorPrimaryGreen, R.color.colorPrimaryGreen, R.color.normalTabColorGreen, R.string.avocado, ThemeIcon.None, true),
    PureDark(13, R.color.colorPrimaryPureDark, R.color.colorPrimaryPureDark, R.color.normalTabColorDark, R.string.pure_dark, ThemeIcon.None, true);

    public final int code;
    public final int colorResourceId;
    public final int selectedTextColorResourceId;
    public final int stringResourceId;
    public final int normalTabColorResourceId;
    public final ThemeIcon themeIcon;
    public final boolean premium;

    Theme(int code, int colorResourceId, int selectedTextColorResourceId, int normalTabColorResourceId, int stringResourceId, ThemeIcon themeIcon, boolean premium) {
        this.code = code;
        this.colorResourceId = colorResourceId;
        this.selectedTextColorResourceId = selectedTextColorResourceId;
        this.normalTabColorResourceId = normalTabColorResourceId;
        this.stringResourceId = stringResourceId;
        this.themeIcon = themeIcon;
        this.premium = premium;
    }

    public static final Parcelable.Creator<Theme> CREATOR = new Parcelable.Creator<Theme>() {
        public Theme createFromParcel(Parcel in) {
            return Theme.valueOf(in.readString());
        }

        public Theme[] newArray(int size) {
            return new Theme[size];
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

    public boolean isSameFamily(Theme theme) {
        if (theme == this) {
            return true;
        }

        Theme t0 = theme;
        Theme t1 = this;

        if (t0 == Theme.PinkBlack) {
            t0 = Theme.Pink;
        } else if (t0 == Theme.PurpleBlack) {
            t0 = Theme.Purple;
        } else if (t0 == Theme.YellowBlack) {
            t0 = Theme.Yellow;
        }

        if (t1 == Theme.PinkBlack) {
            t1 = Theme.Pink;
        } else if (t1 == Theme.PurpleBlack) {
            t1 = Theme.Purple;
        } else if (t1 == Theme.YellowBlack) {
            t1 = Theme.Yellow;
        }

        return t0 == t1;
    }

    public static Theme[] getValuesForDemo() {
        return new Theme[] {
                White,
                Purple,
                PurpleBlack,
                Yellow,
                YellowBlack,
                Red,
                Blue,
                Green,
                PureDark
        };
    }

    public static Theme[] getValuesForArrayAdapter() {
        return new Theme[] {
                Brown,
                Black,
                Pink,
                Dark,
                White,
                Purple,
                Yellow,
                Red,
                Blue,
                Green,
                PureDark
        };
    }

    public static Theme[] getValuesForArrayAdapter(Theme theme) {
        if (theme == Theme.Pink) {
            return new Theme[] {
                    PinkBlack,
                    Pink
            };
        } else if (theme == Theme.Purple) {
            return new Theme[] {
                    PurpleBlack,
                    Purple
            };
        } else if (theme == Theme.Yellow) {
            return new Theme[] {
                    YellowBlack,
                    Yellow
            };
        } else {
            Assert (false);
        }

        return null;
    }
}