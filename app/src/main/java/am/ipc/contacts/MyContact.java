package am.ipc.contacts;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by haykc on 11/29/2017.
 */

public class MyContact implements Comparable<MyContact>{

    private String fullName;
    private String phone;

    public MyContact() {
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return fullName + ": " + phone;
    }

    @Override
    public int compareTo(@NonNull MyContact c) {
        return this.fullName.compareTo(c.fullName);
    }
}
