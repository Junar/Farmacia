package cl.gob.datos.farmacias.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cl.gob.datos.farmacias.R;

public class InformationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_information,
                container, false);

        TextView txt = (TextView) rootView.findViewById(R.id.information);
        String text = "<p><strong>Farmacias</strong> es una aplicación que facilita la <strong>búsqueda de farmacias de turno</strong> en todo Chile, permitiendo a los ciudadanos acceder a ella en cualquier momento y desde diversos dispositivos móviles.</p><p>Desarrollada por la <strong>Unidad de Modernización del Estado</strong> del Ministerio Secretaría General de la Presidencia de Chile, esta aplicación  ha sido en el marco de la política de <strong>Open Data</strong> impulsada por el gobierno, en base a datos liberados por el <strong>Ministerio de Salud</strong>, los cuales están disponibles para su reutilización en el portal de datos públicos <a href=\"http://datos.gob.cl\">datos.gob.cl</a>.</p><p><a href=\"http://www.modernizacion.gob.cl\">www.modernizacion.gob.cl</a></p>";
        txt.setText(Html.fromHtml(text));

        TextView version = (TextView) rootView.findViewById(R.id.txtVersion);
        version.setText(version.getText() + " "
                + getString(R.string.version_number));

        return rootView;
    }
}
