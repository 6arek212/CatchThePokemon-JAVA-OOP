package assignemt4.models;

import assignemt4.json_models.AgentJson;
import assignemt4.json_models.InfoJson;
import com.google.gson.Gson;

public class Info {
    private int pokemons;
    private boolean is_logged_inalse;
    private int moves;
    private int grade;
    private int game_level;
    private int max_user_level1;
    private int id;
    private String graph;
    private int agents;

    public static Info load(String json){
        InfoJson j = new Gson().fromJson(json, InfoJson.class);
        return j.info;
    }


    public int getPokemons() {
        return pokemons;
    }

    public boolean isIs_logged_inalse() {
        return is_logged_inalse;
    }

    public int getMoves() {
        return moves;
    }

    public int getGrade() {
        return grade;
    }

    public int getGame_level() {
        return game_level;
    }

    public int getMax_user_level1() {
        return max_user_level1;
    }

    public int getId() {
        return id;
    }

    public String getGraphata() {
        return graph;
    }

    public int getAgents() {
        return agents;
    }

    @Override
    public String toString() {
        return "Info{" +
                "pokemons=" + pokemons +
                ", is_logged_inalse=" + is_logged_inalse +
                ", moves=" + moves +
                ", grade=" + grade +
                ", game_level=" + game_level +
                ", max_user_level1=" + max_user_level1 +
                ", id=" + id +
                ", graph='" + graph + '\'' +
                ", agents=" + agents +
                '}';
    }
}
