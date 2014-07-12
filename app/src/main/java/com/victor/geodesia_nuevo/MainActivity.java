package com.victor.geodesia_nuevo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("DefaultLocale") public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Log.i("onNavigationDrawerListener", "position: " + position);
        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PresentationFragment.newInstance())
//                .commit();

        switch (position)
        {
            case 0:
                fragmentManager.beginTransaction().replace(R.id.container, PresentationFragment.newInstance()).commit();
                break;
            case 1:
                fragmentManager.beginTransaction().replace(R.id.container, GeodeticUtmFragment.newInstance()).commit();
                break;
            case 2:
                fragmentManager.beginTransaction().replace(R.id.container, UtmGeodeticFragment.newInstance()).commit();
                break;
            default:
                fragmentManager.beginTransaction().replace(R.id.container, PresentationFragment.newInstance()).commit();
                break;
        }

        onSectionAttached(position);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.app_name);
                break;
            case 1:
            case 2:
                mTitle = getString(R.string.conversion_coordenadas);
                break;
            case 3:
            case 4:
                mTitle = getString(R.string.anamorfosis_lineal);
                break;
            case 5:
            case 6:
                mTitle = getString(R.string.convergencia);
                break;
            case 7:
            case 8:
                mTitle = getString(R.string.distancias);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


//----- FRAGMENTS AND OTHER CLASSES -------------------------------------------------------------------------------
    /*
     *
     *  DIALOGS
     *
     */

    public static class DataBaseDialogFragment extends DialogFragment
    {
        public DataBaseDialogFragment(){}

        //----- ELEMENTS
        EditText edtPointData;
        EditText edtLandaMc;
        ListView lstFoundedPoints;
        Button btnCancel;



        //----- VARIABLES
        //----- Table to read is defined
        Uri camposUri;
        //----- ContentResolver is initialized for access to data base
        ContentResolver cr;
        FoundPointsAdapter adapter;
        ArrayList<FoundPointsModel> arrayFoundPoints= new ArrayList<FoundPointsModel>();


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View view = inflater.inflate(R.layout.view_dialog_data_base, container);
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            edtPointData = (EditText) view.findViewById(R.id.editText1);
            edtLandaMc = (EditText) view.findViewById(R.id.editText2);
            lstFoundedPoints = (ListView) view.findViewById(R.id.listView1);
            btnCancel = (Button) view.findViewById(R.id.button1);

            return view;
        }

        @Override
        public void onActivityCreated(Bundle arg0)
        {
            super.onActivityCreated(arg0);

            try
            {
                camposUri = Uri.parse("content://com.victor.geodesia/geodesia");
                cr = getActivity().getContentResolver();

                String[] campos = new String[] {"proyecto", "nombrePunto", "phi", "landa", "x", "y"};
                Cursor c = cr.query(camposUri, campos, null, null, null);

                FoundPointsModel foundPointList[] = new FoundPointsModel[c.getCount()];
                int i = 0;

                if(c.moveToFirst())
                {
                    do
                    {
                        String project = c.getString(0);
                        String pointName = c.getString(1);
                        String phi = c.getString(2);
                        String landa = c.getString(3);
                        String x = c.getString(4);
                        String y = c.getString(5);

                        arrayFoundPoints.add(new FoundPointsModel(project, pointName, phi, landa, x, y));
                        i = i + 1;
                    }
                    while(c.moveToNext());

                    adapter = new FoundPointsAdapter(getActivity(), arrayFoundPoints);
                    lstFoundedPoints.setAdapter(adapter);
                  //  lstFoundedPoints.setTextFilterEnabled(true);

                    edtPointData.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                            adapter.getFilter().filter(charSequence.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }

                lstFoundedPoints.setOnItemClickListener(new OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View selectedPointDialogView = inflater.inflate(R.layout.view_dialog_landa_mc, null);

                        final EditText edtLantaMc = (EditText) selectedPointDialogView.findViewById(R.id.editText1);
                        Button btnAccept = (Button) selectedPointDialogView.findViewById(R.id.button1);

                        btnAccept.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View arg0) {
                                if(!edtLantaMc.getText().toString().contentEquals(""))
                                {
                                    String pointProject = arrayFoundPoints.get(i).getProject();
                                    String pointName = arrayFoundPoints.get(i).getPointName();
                                    String pointLatitude = arrayFoundPoints.get(i).getLatitude();
                                    String pointLongitude = arrayFoundPoints.get(i).getLongitude();
                                    String pointX = arrayFoundPoints.get(i).getX();
                                    String pointY = arrayFoundPoints.get(i).getY();
                                    String pointLandaMc = edtLandaMc.getText().toString();
                                }
                            }
                        });
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
            }
            catch (Exception e)
            {
                FoundPointsModel nodbObject[] = new FoundPointsModel[1];
                nodbObject[0] = new FoundPointsModel("", getString(R.string.atencion), getString(R.string.no_base_datos), "", "", "");
                arrayFoundPoints.add(nodbObject[0]);
                adapter = new FoundPointsAdapter(getActivity(), arrayFoundPoints);
                lstFoundedPoints.setAdapter(adapter);

                edtPointData.setEnabled(false);
                edtLandaMc.setEnabled(false);
                btnCancel.setText(getString(R.string.comprar));
                btnCancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View arg0) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        String ruta = "http://www.geosoftware.es";
                        i.setData(Uri.parse(ruta));
                        startActivity(i);
                        dismiss();
                    }
                });
            }



        }

        private class FoundPointsAdapter extends ArrayAdapter<FoundPointsModel>
        {
            Activity contextActivity;

            private ArrayList<FoundPointsModel> originalFileList;
            private ArrayList<FoundPointsModel> fileList;
            private DataPointsFilter filter;

            public FoundPointsAdapter(Activity context, ArrayList<FoundPointsModel> fileList)
            {
                super(context, R.layout.adapter_listview_found_points, fileList);
                this.contextActivity = context;

                this.fileList = new ArrayList<FoundPointsModel>();
                this.fileList.addAll(fileList);
                this.originalFileList = new ArrayList<FoundPointsModel>();
                this.originalFileList.addAll(fileList);
            }

            @Override
            public Filter getFilter()
            {
                if(filter == null)
                {
                    filter = new DataPointsFilter();
                }

                return filter;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                ViewHolder holder = null;

                if(convertView == null)
                {
                    LayoutInflater inflater = contextActivity.getLayoutInflater();
                    convertView = inflater.inflate(R.layout.adapter_listview_found_points, null);

                    holder = new ViewHolder();

                    holder.lblPointName = (TextView) convertView.findViewById(R.id.textView1);
                    holder.lblProject = (TextView) convertView.findViewById(R.id.textView4);
                    holder.lblLatitude = (TextView) convertView.findViewById(R.id.textView2);
                    holder.lblLongitude = (TextView) convertView.findViewById(R.id.textView3);
                    holder.lblX = (TextView) convertView.findViewById(R.id.textView5);
                    holder.lblY = (TextView) convertView.findViewById(R.id.textView6);

                    convertView.setTag(holder);
                }
                else
                {
                    holder = (ViewHolder) convertView.getTag();
                }

                FoundPointsModel point = fileList.get(position);
                holder.lblPointName.setText(point.getPointName());
                holder.lblProject.setText(point.getProject());
                holder.lblLatitude.setText(point.getLatitude());
                holder.lblLongitude.setText(point.getLongitude());
                holder.lblX.setText(point.getX() + " m");
                holder.lblY.setText(point.getY() + " m");

                return convertView;
            }

            private class DataPointsFilter extends Filter
            {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence)
                {
                    charSequence = charSequence.toString().toLowerCase();
                    FilterResults result = new FilterResults();

                    if(charSequence != null && charSequence.toString().length() > 0)
                    {
                        ArrayList<FoundPointsModel> filteredItems = new ArrayList<FoundPointsModel>();

                        for(int i = 0, l = originalFileList.size(); i < l; i++)
                        {
                            FoundPointsModel point = originalFileList.get(i);

                            if(point.getProject().toLowerCase().contains(charSequence) ||
                                    point.getPointName().toLowerCase().contains(charSequence))
                            {
                                filteredItems.add(point);
                            }
                        }

                        result.count = filteredItems.size();
                        result.values = filteredItems;
                    }
                    else
                    {
                        synchronized (this)
                        {
                            result.values = originalFileList;
                            result.count = originalFileList.size();
                        }
                    }

                    return result;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults)
                {
                    fileList = (ArrayList<FoundPointsModel>) filterResults.values;
                    notifyDataSetChanged();
                    clear();
                    Log.i("publishResults","Starting to publish: " + fileList);
                    for(int i = 0, l = fileList.size(); i < l; i++) {
                        add(fileList.get(i));
                    }

                    notifyDataSetInvalidated();
                }
            }

        }

        private class ViewHolder
        {
            TextView lblPointName;
            TextView lblProject;
            TextView lblLatitude;
            TextView lblLongitude;
            TextView lblX;
            TextView lblY;
        }
    }


    public static class NavigatorDialogFragment extends DialogFragment
    {
        public NavigatorDialogFragment(){}

        //----- Elements
        ListView lstFoldersFiles;
        Button btnCancel;
        Button btnBack;
        TextView txtCurrentRoot;


        //----- Variables
        private ArrayList<String> paths = null; // Aqui se van almacenando las rutas
        private String root="/";
        private File navigatorFile;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View view = inflater.inflate(R.layout.view_file_navigator, container);
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            lstFoldersFiles = (ListView) view.findViewById(R.id.listView1);
            btnCancel = (Button) view.findViewById(R.id.button1);
            btnBack = (Button) view.findViewById(R.id.button2);
            txtCurrentRoot = (TextView) view.findViewById(R.id.textView2);

            getDir(root);

            return view;
        }

        @Override
        public void onActivityCreated(Bundle arg0)
        {
            // TODO Auto-generated method stub
            super.onActivityCreated(arg0);

            lstFoldersFiles.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                {
                    navigatorFile = new File(paths.get(arg2));

                    if(navigatorFile.isDirectory())
                    {
                        if(navigatorFile.canRead())
                        {
                            getDir(paths.get(arg2));
                        }
                        else
                        {
                            Toast.makeText(getActivity(), getString(R.string.accion_no_permitida), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        String typeFile = MimeTypeMap.getFileExtensionFromUrl(navigatorFile.getAbsolutePath());

                        if(typeFile.contentEquals("txt"))
                        {
                            ArrayList<String> readData = readFile(navigatorFile.getAbsolutePath());
                        }
                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dismiss();
                }
            });

            btnBack.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.i("navigator", "path: " + navigatorFile.getAbsolutePath());
                    if(!navigatorFile.getAbsolutePath().toString().contentEquals(root))
                    {
                        navigatorFile = new File(navigatorFile.getParent());
                        getDir(navigatorFile.getPath());
                    }
                }
            });
        }

        private void getDir(String dirPath)
        {
            txtCurrentRoot.setText(getString(R.string.ruta_actual) + ": " + dirPath);

            paths = new ArrayList<String>();
//            File f = new File(dirPath);
//            File[] files = f.listFiles();
            navigatorFile = new File(dirPath);
            File files[] = navigatorFile.listFiles();
            NavigatorModel[] items = new NavigatorModel[files.length];

            for(int i = 0; i < files.length; i++)
            {
                File file = files[i];
                paths.add(file.getPath());

                items[i] = new NavigatorModel(file.isDirectory(), file.getAbsolutePath());
            }

            NavigatorAdapter adapter = new NavigatorAdapter(getActivity(), items);
            lstFoldersFiles.setAdapter(adapter);
        }

        private ArrayList<String> readFile (String nombre)
        {
            File file = new File(nombre);
            ArrayList<String> lines = new ArrayList<String>();

            try
            {
                FileInputStream fIn = new FileInputStream(file);
                InputStreamReader readerFile = new InputStreamReader(fIn);
                BufferedReader br = new BufferedReader(readerFile);
                String line = br.readLine();
                lines.add(line);

                while (line != null)
                {
                    line  = br.readLine();
                    lines.add(line);
                }

                br.close();
                readerFile.close();
                return lines;
            }
            catch (Exception e)
            {
                e.getStackTrace();
                return null;
            }
        }


        private class NavigatorAdapter extends ArrayAdapter<NavigatorModel>
        {
            Activity contextActivity;
            NavigatorModel[] fileList;

            public NavigatorAdapter(Activity context, NavigatorModel[] fileList)
            {
                super(context, R.layout.adapter_view_file_navigator, fileList);
                this.contextActivity = context;
                this.fileList = fileList;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                LayoutInflater inflater = contextActivity.getLayoutInflater();
                View item = inflater.inflate(R.layout.adapter_view_file_navigator, null);

                TextView lblTittle = (TextView) item.findViewById(R.id.textView1);
                TextView lblSubTitle = (TextView) item.findViewById(R.id.textView2);
                ImageView imageView = (ImageView)item.findViewById(R.id.imageView1);

                //----- Evaluar que archivo es para pintar una imagen u otra.
                boolean isDirectory = fileList[position].isDirectory();
                String filePath = fileList[position].getName();
                File file = new File(filePath);

                String nameItem = file.getName();
                String typeItem = MimeTypeMap.getFileExtensionFromUrl(filePath);

                lblTittle.setText(nameItem.toUpperCase());
                if(isDirectory)
                {
                    imageView.setImageResource(R.drawable.ic_carpeta);
                    lblSubTitle.setText(getString(R.string.carpeta));
                }
                else
                {
                    if(typeItem.contentEquals("txt"))
                    {
                        imageView.setImageResource(R.drawable.ic_archivo);
                        lblSubTitle.setText(getString(R.string.archivo_texto));
                    }
                    else
                    {
                        imageView.setImageResource(R.drawable.ic_desconocido);
                        lblSubTitle.setText(getString(R.string.archivo_desconocido));
                    }
                }


                return item;
            }
        }
    }

    /*
     *
     * Main fragment
     *
     */

    public static class PresentationFragment extends Fragment
    {
        //----- View elements declaration
        Activity presentationActivity;
        View rootView;
        LinearLayout lyExamples;


        public static PresentationFragment newInstance()
        {
            PresentationFragment fragment = new PresentationFragment();
            return fragment;
        }

        public PresentationFragment(){}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
            lyExamples = (LinearLayout) rootView.findViewById(R.id.lyExamples);
            presentationActivity = getActivity();

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);

            lyExamples.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // Animación para el layout
                    AlphaAnimation animation = new AlphaAnimation(0.2f, 1.0f);
                    animation.setDuration(1000);
                    animation.setFillAfter(true);
                    lyExamples.startAnimation(animation);

                    // Dialog con listview de opciones
                    AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View layout = inflater.inflate(R.layout.txt_example_view, null);
                    builder.setView(layout);
                    final AlertDialog examplesDialog = builder.create();

                    ListView lstExamples = (ListView) layout.findViewById(R.id.listView1);

                    SideBarModel[] toolsList = new SideBarModel[]
                            {
                                    new SideBarModel(getString(R.string.conversion_coordenadas), getString(R.string.geodesicas_utm)),
                                    new SideBarModel(getString(R.string.conversion_coordenadas), getString(R.string.utm_geodesicas)),
                                    new SideBarModel(getString(R.string.anamorfosis_lineal), getString(R.string.coordenadas_geodesicas)),
                                    new SideBarModel(getString(R.string.anamorfosis_lineal), getString(R.string.coordenadas_utm)),
                                    new SideBarModel(getString(R.string.convergencia), getString(R.string.coordenadas_geodesicas)),
                                    new SideBarModel(getString(R.string.convergencia), getString(R.string.coordenadas_utm)),
                                    new SideBarModel(getString(R.string.distancias), getString(R.string.reducida_utm)),
                                    new SideBarModel(getString(R.string.distancias), getString(R.string.utm_reducida))
                            };
                    SideListAdapter adapter = new SideListAdapter(presentationActivity, toolsList);
                    lstExamples.setAdapter(adapter);

                    lstExamples.setOnItemClickListener(new OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                        {
                            // Aquí van los métodos para imprimir todos los ejemplos de fichero.txt


                            examplesDialog.dismiss();
                        }
                    });

                    examplesDialog.show();
                }
            });
        }

        class SideListAdapter extends ArrayAdapter<SideBarModel>
        {
            Activity contextActivity;
            SideBarModel[] toolsList;

            public SideListAdapter(Activity context, SideBarModel[] toolsList)
            {
                super(context, R.layout.row_adapter_side_list, toolsList);
                this.contextActivity = context;
                this.toolsList = toolsList;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                LayoutInflater inflater = contextActivity.getLayoutInflater();
                View item = inflater.inflate(R.layout.row_adapter_side_list, null);

                TextView lblTittle = (TextView) item.findViewById(R.id.textView1);
                TextView lblSubTitle = (TextView) item.findViewById(R.id.textView2);

                String title = toolsList[position].getTitle();
                String subTitle = toolsList[position].getSubtTitle();

                lblTittle.setText(title);
                lblSubTitle.setText(subTitle);

                return item;
            }
        }
    }


    /*
     *
     * Geodetic to UTM conversor fragment
     *
     */

    public static class GeodeticUtmFragment extends Fragment
    {
        //----- View elements declaration
        View rootView;
        Button btnFromFile;
        Button btnInputCoordinate;
        ListView lstAddedCoordinates;
        Button btnCalculate;
        Button btnDeleteAll;
        ListView lstCalculatedPoints;


        //----- Variables declaration
        ArrayList<String> addedPointsArrayList = new ArrayList<String>();
        ArrayList<String> calculatedPointsArrayList = new ArrayList<String>();



        public static GeodeticUtmFragment newInstance()
        {
            GeodeticUtmFragment fragment = new GeodeticUtmFragment();
            return fragment;
        }

        public GeodeticUtmFragment(){}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            rootView = inflater.inflate(R.layout.geodetic_utm, container, false);
            btnFromFile = (Button) rootView.findViewById(R.id.button3);
            btnInputCoordinate = (Button) rootView.findViewById(R.id.button4);
            lstAddedCoordinates = (ListView) rootView.findViewById(R.id.listView1);
            btnDeleteAll = (Button) rootView.findViewById(R.id.button1);
            btnCalculate = (Button) rootView.findViewById(R.id.button2);
            lstCalculatedPoints = (ListView) rootView.findViewById(R.id.listView2);

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);

            btnFromFile.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.Selecciona_fuente));

                    builder.setPositiveButton(getString(R.string.base_datos), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DataBaseDialogFragment dbFragment = new DataBaseDialogFragment();
                            dbFragment.show(getFragmentManager(), "DataBaseFragment");
                        }
                    });

                    builder.setNegativeButton(getString(R.string.fichero_de_texto), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which) {
                            NavigatorDialogFragment dialogFragment =  new NavigatorDialogFragment();
                            dialogFragment.show(getFragmentManager(), "NavigatorDialog");
                        }
                    });

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });

            btnInputCoordinate.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View layout = inflater.inflate(R.layout.view_input_geodetic_coordinate, null);
                    builder.setView(layout);
                    final AlertDialog inputCoordinateDialog = builder.create();

                    final EditText txtLatitudeGrades = (EditText) layout.findViewById(R.id.editText1);
                    final EditText txtLatitudeMinutes = (EditText) layout.findViewById(R.id.editText2);
                    final EditText txtLatitudeSecconds = (EditText) layout.findViewById(R.id.editText3);

                    final EditText txtLongitudeGrades = (EditText) layout.findViewById(R.id.editText4);
                    final EditText txtLongitudeMinutes = (EditText) layout.findViewById(R.id.editText5);
                    final EditText txtLongitudeSecconds = (EditText) layout.findViewById(R.id.editText6);

                    final EditText txtLongitudeMc = (EditText) layout.findViewById(R.id.editText7);

                    Button btnCancel = (Button) layout.findViewById(R.id.button1);
                    Button btnAccept = (Button) layout.findViewById(R.id.button2);

                    btnCancel.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            inputCoordinateDialog.dismiss();
                        }
                    });

                    btnAccept.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            String strLatitudeGrades = txtLatitudeGrades.getText().toString();
                            String strLatitudeMinutes = txtLatitudeMinutes.getText().toString();
                            String strLatitudeSecconds = txtLatitudeSecconds.getText().toString();
                            String strLongitudeGrades = txtLongitudeGrades.getText().toString();
                            String strLongitudeMinutes = txtLongitudeMinutes.getText().toString();
                            String strLongitudeSecconds = txtLongitudeSecconds.getText().toString();
                            String strLongitudeMc = txtLongitudeMc.getText().toString();

                            if(strLatitudeGrades.contentEquals("") || strLatitudeMinutes.contentEquals("") || strLatitudeSecconds.contentEquals("")
                                    || strLongitudeGrades.contentEquals("") || strLongitudeMinutes.contentEquals("") || strLongitudeSecconds.contentEquals("")
                                    || strLongitudeMc.contentEquals(""))
                            {
                                Toast.makeText(getActivity(), getText(R.string.no_valores_vacios), Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                addedPointsArrayList.add(strLatitudeGrades + " " + strLatitudeMinutes + " " + strLatitudeSecconds + " "
                                        + strLongitudeGrades + " " + strLongitudeMinutes + " " + strLongitudeSecconds + " " + strLongitudeMc);
                                geodeticListRefresh(addedPointsArrayList, lstAddedCoordinates);

                                inputCoordinateDialog.dismiss();
                            }
                        }
                    });

                    inputCoordinateDialog.show();
                }
            });

            btnDeleteAll.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    addedPointsArrayList.clear();
                    calculatedPointsArrayList.clear();

                    geodeticListRefresh(addedPointsArrayList, lstAddedCoordinates);
//					pointsListRefresh(calculatedPointsArrayList, lstCalculatedPoints); Aqui añadir el listado de UTM
                }
            });

            btnCalculate.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    // TODO Auto-generated method stub

                }
            });
        }

        public void geodeticListRefresh(ArrayList<String> list, ListView listView)
        {
            ArrayList<String> formattedData = new ArrayList<String>();

            for(int i = 0; i <  list.size(); i++)
            {
                String[] data = list.get(i).split(" ");
                formattedData.add(data[0] + "º " + data[1] + "' " + data[2] + "''" + "  "
                        + data[3] + "º " + data[4] + "' " + data[5]+ "''" + "  "
                        + data[6] + "º");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, formattedData);
            listView.setAdapter(adapter);
        }
    }


    /**
     *
     *
     * UTM to Geodetic conversor fragment
     *
     * **/

    public static class UtmGeodeticFragment extends Fragment
    {
        //----- View elements declaration
        View rootView;

        public static UtmGeodeticFragment newInstance()
        {
            UtmGeodeticFragment fragment = new UtmGeodeticFragment();
            return fragment;
        }

        public UtmGeodeticFragment(){};

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            rootView = inflater.inflate(R.layout.utm_geodetic, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            // TODO Auto-generated method stub
            super.onActivityCreated(savedInstanceState);
        }
    }

}