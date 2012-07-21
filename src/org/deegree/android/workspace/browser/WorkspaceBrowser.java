package org.deegree.android.workspace.browser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WorkspaceBrowser extends FragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory.
     * If this becomes too memory intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_workspace_browser );
        // Create the adapter that will return a fragment for each of the three
        // primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter( getSupportFragmentManager() );

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById( R.id.pager );
        mViewPager.setAdapter( mSectionsPagerAdapter );

        // When swiping between different sections, select the corresponding
        // tab.
        // We can also use ActionBar.Tab#select() to do this if we have a
        // reference to the
        // Tab.
        mViewPager.setOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected( int position ) {
                actionBar.setSelectedNavigationItem( position );
            }
        } );

        // For each of the sections in the app, add a tab to the action bar.
        for ( int i = 0; i < mSectionsPagerAdapter.getCount(); i++ ) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter.
            // Also specify this Activity object, which implements the
            // TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab( actionBar.newTab().setText( mSectionsPagerAdapter.getPageTitle( i ) ).setTabListener( this ) );
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate( R.menu.activity_workspace_browser, menu );
        return true;
    }

    @Override
    public void onTabUnselected( ActionBar.Tab tab, FragmentTransaction fragmentTransaction ) {
    }

    @Override
    public void onTabSelected( ActionBar.Tab tab, FragmentTransaction fragmentTransaction ) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem( tab.getPosition() );
    }

    @Override
    public void onTabReselected( ActionBar.Tab tab, FragmentTransaction fragmentTransaction ) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter( FragmentManager fm ) {
            super( fm );
        }

        @Override
        public Fragment getItem( int i ) {
            Fragment fragment = new WorkspaceListFragment();
            Bundle args = new Bundle();
            if ( i == 0 ) {
                args.putString( "url", "http://download.deegree.org/deegree3/workspaces/workspaces-3.2-pre9-SNAPSHOT" );
            } else {
                args.putString( "url", "http://download.occamlabs.de/workspaces/occamlabs-workspaces" );
            }
            fragment.setArguments( args );
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle( int position ) {
            switch ( position ) {
            case 0:
                return getString( R.string.standard_workspaces ).toUpperCase();
            case 1:
                return getString( R.string.occamlabs_workspaces ).toUpperCase();
            }
            return null;
        }
    }

    public static class WorkspaceListFragment extends Fragment {
        @Override
        public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
            try {
                Bundle b = getArguments();

                LinearLayout layout = new LinearLayout( getActivity() );
                layout.setOrientation( LinearLayout.VERTICAL );

                new DownloadWorkspaceList( layout ).execute( b.getString( "url" ) );

                return layout;
            } catch ( Throwable t ) {
                Log.e( getClass().getName(), t.getLocalizedMessage(), t );
                TextView textView = new TextView( getActivity() );
                textView.setGravity( Gravity.TOP );
                textView.setText( R.string.workspaces_not_loaded );
                return textView;
            }
        }
    }

    public static class DownloadWorkspaceList extends AsyncTask<String, String, List<String>> {
        private ViewGroup layout;

        public DownloadWorkspaceList( ViewGroup parent ) {
            this.layout = parent;
        }

        @Override
        protected List<String> doInBackground( String... url ) {
            try {
                URL u = new URL( url[0] );
                BufferedReader in = new BufferedReader( new InputStreamReader( u.openStream() ) );
                List<String> workspaces = new ArrayList<String>();
                String s = null;
                while ( ( s = in.readLine() ) != null ) {
                    workspaces.add( s.split( "\\s", 2 )[1] );
                }

                in.close();

                return workspaces;
            } catch ( Throwable t ) {
                Log.e( getClass().getName(), t.getLocalizedMessage(), t );
                return new ArrayList<String>();
            }
        }

        @Override
        protected void onPostExecute( List<String> result ) {
            for ( String s : result ) {
                TextView textView = new TextView( layout.getContext() );
                textView.setGravity( Gravity.TOP );
                textView.setText( s );
                layout.addView( textView );
            }
        }
    }

}
