package cl.gob.datos.farmacias.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import cl.gob.datos.farmacias.R;
import cl.gob.datos.farmacias.adapter.CustomPharmaAdapter;
import cl.gob.datos.farmacias.controller.AppController;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip.IconTabProvider;

public class MainActivity extends FragmentActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private PagerSlidingTabStrip tabs;

    private MenuItem searchMenuItem;
    private Menu mMenu;
    private int selectedPage = 0;

    public int getSelectedPage() {
        return selectedPage;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.pharmacy_list, menu);
        mMenu = menu;
        final PharmaListFragment listado = (PharmaListFragment) getSupportFragmentManager()
                .findFragmentByTag("listadofarmacias");
        final PharmaListFragment listFromMap = (PharmaListFragment) getSupportFragmentManager()
                .findFragmentByTag("listadodesdemap");
        MenuItem searchItem = mMenu.findItem(R.id.search);

        if (searchItem != null
                && ((selectedPage == 0 && listado != null && listado
                        .isVisible()) || (selectedPage == 1
                        && listFromMap != null && listFromMap.isVisible()))) {
            searchMenuItem = searchItem;

            SearchView searchView = (SearchView) MenuItemCompat
                    .getActionView(searchMenuItem);

            searchView.setOnQueryTextListener(new OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String arg0) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String arg0) {
                    CustomPharmaAdapter adapter = null;
                    if (listado != null)
                        adapter = ((CustomPharmaAdapter) listado
                                .getListAdapter());
                    CustomPharmaAdapter adapterFromMap = null;
                    if (listFromMap != null)
                        adapterFromMap = ((CustomPharmaAdapter) listFromMap
                                .getListAdapter());
                    if (adapter != null && selectedPage == 0) {
                        adapter.getFilter().filter(arg0);
                        if (adapterFromMap != null)
                            adapterFromMap.getFilter().filter("");
                    } else if (adapterFromMap != null && selectedPage == 1) {
                        adapterFromMap.getFilter().filter(arg0);
                        if (adapter != null)
                            adapter.getFilter().filter("");
                    }
                    return false;
                }
            });
        }

        return true;
    }

    private void showUp(boolean show) {
        getActionBar().setHomeButtonEnabled(show);
        getActionBar().setDisplayHomeAsUpEnabled(show);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        switch (selectedPage) {
        case 0:
            mMenu.setGroupVisible(0, false);
            Fragment listado = getSupportFragmentManager().findFragmentByTag(
                    "listadofarmacias");
            Fragment mapaFromListado = getSupportFragmentManager()
                    .findFragmentByTag("mapadesdelistado");
            if (listado != null && listado.isVisible()) {
                mMenu.findItem(R.id.search).setVisible(true);
                mMenu.findItem(R.id.menu_map_settings).setVisible(false);
                mMenu.findItem(R.id.action_switch_mode)
                        .setIcon(R.drawable.ic_location_map).setVisible(true);
                showUp(true);
            } else if (mapaFromListado != null && mapaFromListado.isVisible()) {
                mMenu.findItem(R.id.search).setVisible(false);
                mMenu.findItem(R.id.menu_map_settings).setVisible(false);
                mMenu.findItem(R.id.action_switch_mode)
                        .setIcon(R.drawable.ic_collections_sort_by_size)
                        .setVisible(true);
                showUp(true);
            } else {
                showUp(false);
            }

            break;
        case 1:
            mMenu.findItem(R.id.search).setVisible(false);
            mMenu.findItem(R.id.menu_map_settings).setVisible(true);
            mMenu.findItem(R.id.action_switch_mode)
                    .setIcon(R.drawable.ic_collections_sort_by_size)
                    .setVisible(true);
            Fragment listadoFromMap = getSupportFragmentManager()
                    .findFragmentByTag("listadodesdemap");

            if (listadoFromMap != null && listadoFromMap.isVisible()) {
                mMenu.findItem(R.id.search).setVisible(true);
                mMenu.findItem(R.id.menu_map_settings).setVisible(false);
                mMenu.findItem(R.id.action_switch_mode)
                        .setIcon(R.drawable.ic_location_map).setVisible(true);
                showUp(true);
            } else {
                showUp(false);
            }

            break;
        default:
            mMenu.setGroupVisible(0, false);
            showUp(false);
            break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        Fragment search = getSupportFragmentManager().findFragmentByTag("zero");
        Fragment map = getSupportFragmentManager().findFragmentByTag("mapa");
        Fragment listFromMap = getSupportFragmentManager().findFragmentByTag(
                "listadodesdemap");
        Fragment mapFromList = getSupportFragmentManager().findFragmentByTag(
                "mapadesdelistado");
        Fragment listado = getSupportFragmentManager().findFragmentByTag(
                "listadofarmacias");
        if ((selectedPage == 0 && search != null && search.isVisible())
                || (selectedPage == 1 && map != null && map.isVisible())
                || selectedPage == 2) {
            finish();
        } else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (selectedPage == 1 && listFromMap != null
                    && listFromMap.isVisible()) {
                ft.replace(R.id.frames_map_container, map).commit();
            } else if (selectedPage == 0 && mapFromList != null
                    && mapFromList.isVisible()) {
                ft.replace(R.id.frames_container, listado).commit();
            } else if (selectedPage == 0 && listado != null
                    && listado.isVisible()) {
                ft.replace(R.id.frames_container, search).commit();
            } else {
                super.onBackPressed();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment map = getSupportFragmentManager().findFragmentByTag("mapa");
        switch (item.getItemId()) {
        case android.R.id.home:
        case R.id.action_switch_mode:
            Fragment listado = getSupportFragmentManager().findFragmentByTag(
                    "listadofarmacias");
            Fragment mapFromList = getSupportFragmentManager()
                    .findFragmentByTag("mapadesdelistado");
            Fragment listFromMap = getSupportFragmentManager()
                    .findFragmentByTag("listadodesdemap");
            if (listado != null && listado.isVisible() && selectedPage == 0) {
                if (item.getItemId() == R.id.action_switch_mode) {
                    ((PharmaListFragment) listado).openMap();
                } else {
                    showUp(false);
                    onBackPressed();
                }
            } else if (mapFromList != null && mapFromList.isVisible()
                    && selectedPage == 0) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frames_container, listado).commit();
            } else if (map != null && map.isVisible() && selectedPage == 1) {
                ((PharmaClosestFragment) map).openList();
            } else if (listFromMap != null && listFromMap.isVisible()
                    && selectedPage == 1) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frames_map_container, map).commit();
            }
            break;
        case R.id.menu_map_settings:
            if (map != null && map.isVisible() && selectedPage == 1) {
                ((PharmaClosestFragment) map).showSettingDialog();
            }
        }

        return true;
    }

    @Override
    public boolean onSearchRequested() {
        if (searchMenuItem != null) {
            if (searchMenuItem.isActionViewExpanded()) {
                searchMenuItem.collapseActionView();
            } else {
                searchMenuItem.expandActionView();
            }
        }
        return super.onSearchRequested();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppController.connectLocationClient();
    }

    @Override
    protected void onStop() {
        AppController.disconnectLocationClient();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);
        tabs.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int page) {
                selectedPage = page;
                getActionBar().setTitle(
                        mSectionsPagerAdapter.getPageTitle(selectedPage));
                invalidateOptionsMenu();
                AppController.getLastLocation();
                if ((page == 0 || page == 1) && searchMenuItem != null) {
                    searchMenuItem.collapseActionView();
                    ((SearchView) searchMenuItem.getActionView()).setQuery("",
                            false);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        getActionBar().setTitle(
                mSectionsPagerAdapter.getPageTitle(selectedPage));
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter implements
            IconTabProvider {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
            case 0:
                return new FragmentZero();

            case 1:
                return new FragmentOne();

            default:
                return new InformationFragment();
            }

        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence title = "";
            switch (position) {
            case 0:
                title = getString(R.string.title_section1);
                break;
            case 1:
                title = getString(R.string.title_section2);
                break;
            case 2:
                title = getString(R.string.title_section3);
                break;
            }
            return title;
        }

        @Override
        public int getPageIconResId(int position) {
            switch (position) {
            case 0:
                return R.drawable.ic_action_search;
            case 1:
                return R.drawable.ic_location_web_site;
            case 2:
                return R.drawable.ic_action_about;
            }
            return 0;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_complaint,
                    container, false);
            return rootView;
        }
    }
}
