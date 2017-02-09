package czy.com.newrefreshlayout.adapter;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public FragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragments.addAll(fragments);
    }

    public FragmentAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        mFragments.addAll(fragments);
        mTitles.addAll(titles);
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
    }

    public void removeFragment(Fragment fragment) {
        mFragments.remove(fragment);
    }

    public void setFragments(List<Fragment> fragments) {
        mFragments = fragments;
    }

    public List<Fragment> getFragments() {
        return mFragments;
    }

    public void clear() {
        for (Fragment fragment : mFragments) {
            if (fragment != null && fragment.isAdded()) {
                fragment.onDestroy();
            }
        }
        mFragments.clear();
    }

    public void onSelectedStateChanged(int position) {
        for (int i = 0; i < mFragments.size(); i++) {
            if (mFragments.get(i) instanceof OnSelectedStateChangedListener) {
                ((OnSelectedStateChangedListener) mFragments.get(i)).onSelectedStateChanged(i == position);
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    public interface OnSelectedStateChangedListener {
        void onSelectedStateChanged(boolean selected);
    }

    public void dispatchActivityResult(int requestCode, int resultCode, Intent data) {
        for (int i = 0; i < mFragments.size(); i++) {
            if (mFragments.get(i) instanceof OnSelectedStateChangedListener) {
                mFragments.get(i).onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (!mTitles.isEmpty()) {
            return mTitles.get(position % mTitles.size());
        }
        return super.getPageTitle(position);
    }
}
