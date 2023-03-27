package com.pranayharjai7.myemotions.Utils;

import static com.pranayharjai7.myemotions.FriendsActivity.ADD_FRIENDS;
import static com.pranayharjai7.myemotions.FriendsActivity.FRIEND_REQUESTS;
import static com.pranayharjai7.myemotions.FriendsActivity.MY_FRIENDS;
import static com.pranayharjai7.myemotions.MainActivity.HOME;
import static com.pranayharjai7.myemotions.MainActivity.MAPS;
import static com.pranayharjai7.myemotions.MainActivity.STATS;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.pranayharjai7.myemotions.Fragments.FriendsActivityFragments.MyFriendsFragment;
import com.pranayharjai7.myemotions.Fragments.MainActivityFragments.HomeFragment;
import com.pranayharjai7.myemotions.Fragments.MainActivityFragments.MapsFragment;
import com.pranayharjai7.myemotions.Fragments.MainActivityFragments.StatsFragment;
import com.pranayharjai7.myemotions.R;

public class FragmentUtils {
    public static void replaceMainFragment(FragmentManager fragmentManager, String fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.fade_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.fade_out  // popExit
        );

        switch (fragment) {
            case HOME: {
                transaction.replace(R.id.mainFragmentContainerView, HomeFragment.class, null);
                break;
            }
            case STATS: {
                transaction.replace(R.id.mainFragmentContainerView, StatsFragment.class, null);
                break;
            }
            case MAPS: {
                transaction.replace(R.id.mainFragmentContainerView, MapsFragment.class, null);
                break;
            }
            default: {
                transaction.replace(R.id.mainFragmentContainerView, HomeFragment.class, null);
            }
        }

        transaction.setReorderingAllowed(true)
                //.addToBackStack(fragment)
                .commit();
    }

    public static void replaceFriendsFragment(FragmentManager fragmentManager, String fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.fade_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.fade_out  // popExit
        );

        switch (fragment) {
            case MY_FRIENDS: {
                transaction.replace(R.id.friendsFragmentContainerView, MyFriendsFragment.class, null);
                break;
            }
            case ADD_FRIENDS: {
//                transaction.replace(R.id.friendsFragmentContainerView, StatsFragment.class, null);
                break;
            }
//            case FRIEND_REQUESTS: {
//                transaction.replace(R.id.friendsFragmentContainerView, MapsFragment.class, null);
//                break;
//            }
//            case REMOVE_FRIENDS: {
//                transaction.replace(R.id.friendsFragmentContainerView, MapsFragment.class, null);
//                break;
//            }
            default: {
                transaction.replace(R.id.friendsFragmentContainerView, MyFriendsFragment.class, null);
            }
        }

        transaction.setReorderingAllowed(true)
                //.addToBackStack(fragment)
                .commit();
    }
}
