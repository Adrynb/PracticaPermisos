package es.instituto.practicapermisos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date

class AdaptadorEntrada(private var contexto: Context) :
    ArrayAdapter<Entrada>(contexto, R.layout.filaentrada,  mutableListOf<Entrada>()) {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView
        var time: TextView
        var coordenada: TextView

        lateinit var item: Entrada

        init {
            imagen = view.findViewById(R.id.imageView)
            time = view.findViewById(R.id.time)
            coordenada = view.findViewById(R.id.coordenadas)


        }

    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = this.getItem(position)
        var vista = LayoutInflater.from(getContext()).inflate(R.layout.filaentrada, parent, false);
        vista.findViewById<ImageView>(R.id.imageView).setImageBitmap(item?.imagen)
        vista.findViewById<TextView>(R.id.coordenadas).text="Lat:"+item?.location?.latitude.toString()+" Lon:"+item?.location?.latitude.toString()

        if (item != null ) {
            vista.findViewById<TextView>(R.id.time).text=  SimpleDateFormat("yyyy.MM.dd HH:mm").format(Date(
                item.location!!.time))
        }
        return vista
    }

}