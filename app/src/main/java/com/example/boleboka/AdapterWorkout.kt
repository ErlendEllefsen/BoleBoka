package com.example.boleboka

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.workout_items.view.*

/* Erlend: Ettersom workout- og exersice-list fungerer på samme måte prøvde jeg finne en måte
 * å bruke ett adapter for begge. Dette klarte jeg ikke få til så de har separate adaptere som er
 * helt like.
 * Mye av koden er skrevet med hjelp av guide fra Coding in Flow, referanse i dokumentasjon
 */

class AdapterWorkout(
    private val workoutList: ArrayList<Workout_Item>,
    private val listner: Workouts
) : RecyclerView.Adapter<AdapterWorkout.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Erlend: Layoutinflater gjør xlm-filer om til View-objekter
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.workout_items, parent, false)
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

    /* Erlend: En "Viewholder" representerer ett punkt i listen
     * Altså den inneholder en instanse av layouten workout_items.xlm
     * Den inneholder også annen data om dette punktet i listen.
     * Som feks hvor dette punktet er i listen (posisjonen)
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val workoutView1: TextView = itemView.text_1
        val workoutView2: TextView = itemView.text_2

        //Konstruktør
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            /*
             * Erlend: Når du sletter ett item har det en animasjon før det forsvinner.
             * Om en trykker på ett item mens remove-animasjonen kjører vil den ikke ha en posisjon.
             * Derfor må vi sjekke med en if-setning for å unngå at appen krasjer
             */
            if(position != RecyclerView.NO_POSITION) {
                listner.onItemClick(position)
            }
        }
    }

    //Erlend: Brukes for å sende click event til andre plasser, ikke hensiktsmessig å ha det i adapteren
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}