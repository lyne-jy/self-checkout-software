package ca.ucalgary.seng300.selfcheckout.utility;

/*
 * Simple class that represents a pair of any generic elements
 */
public class Pair<T, G>{

    private T first;
    private G second;

    public Pair(T first, G second){
        this.first = first;
        this.second = second;
    }

    public void setFirst(T newFirst){
        this.first = newFirst;
    }

    public void setSecond(G newSecond){
        this.second = newSecond;
    }

    public T getFirst(){
        return first;
    }

    public G getSecond(){
        return second;
    }
}
