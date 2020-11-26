package com.example.boleboka

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.workout_items.view.*

class AdapterWorkout(private val workoutList: ArrayList<Workout_Item>, private val listner: Workouts) : RecyclerView.Adapter<AdapterWorkout.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.workout_items, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*
         * Blir called hver gang du scroller og nye views kommer inn.
         * Ikke add noe kode i denne, da vil appen bli treg
         * Bruk class ViewHolder
         */

        val currentWorkoutItem = workoutList[position]
        holder.workoutView1.text = currentWorkoutItem.text1
        holder.workoutView2.text = currentWorkoutItem.text2
    }

    override fun getItemCount() = workoutList.size

    // "inner" gjør klassen non-static
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val workoutView1: TextView = itemView.text_1
        val workoutView2: TextView = itemView.text_2


        //Konstruktør
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            /*
             * Når du sletter ett item har det en animasjon før det forsvinner.
             * Om en trykker på ett item mens remove-animasjonen kjører vil den ikke ha en posisjon.
             * Derfor må vi sjekke med en if-setning for å unngå at appen krasjer
             */
            if(position != RecyclerView.NO_POSITION) {
                listner.onItemClick(position)
            }
        }
    }

    //Brukes for å sende click event til andre plasser, ikke hensiktsmessig å ha det i adapteren
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}