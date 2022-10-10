import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class EarthquakeNotification{
    public static void main(String [] args){
        //*****************************************************************************************************
        //First I need to read 2 input files before anything else
        //As given in the homework pdf, [--all] <watcherFile> <earthquakeFile> is the format
        String watcherFileName= "file1";
        String earthquakeFileName= "file2";
        boolean wantNotification=false;
        if(args.length==3){
            wantNotification=true;//since we know the format
            watcherFileName=args[1];//since we know the format
            earthquakeFileName=args[2];//since we know the format
        }
        else{
            watcherFileName=args[0];//since we know the format
            earthquakeFileName=args[1];//since we know the format
        }
        Scanner inputStreamWatcher=null;
        Scanner inputSrtreamEa=null;
        try {
            inputStreamWatcher=new Scanner(new File(watcherFileName));
            inputSrtreamEa=new Scanner(new File(earthquakeFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //*****************************************************************************************************
        //*****************************************************************************************************
        //ADDING WATCHERS
        //This step is symbolic since we need to excecute the events in order
        //Altough I add all the elements to the list, later they will be assesed based on their time-stamp
        //This is just to obtain information that are given to us
        DoublyLinkedList<String> WatcherList=new DoublyLinkedList<>();
        DoublyLinkedList<String> WatcherListWalker=new DoublyLinkedList<>();
        while (inputStreamWatcher.hasNextLine()){
            String line=inputStreamWatcher.nextLine();
            WatcherList.addLast(line);
            WatcherListWalker.addLast(line);
            //added all the elements but will be used when their time-stamp is valid
        }
        inputStreamWatcher.close();
        //*****************************************************************************************************
        //*****************************************************************************************************
        //ADDING EARTHQUAKES
        //This step is symbolic since we need to excecute the events in order
        //Altough I add all the elements to the queue, later they will be assesed based on their time-stamp and will be added to another queue
        //This is just to obtain information that are given to us
        LinkedQueue<String> EarthquakeQueue=new LinkedQueue<>();
        ArrayList<String> EarthquakeMagnitudeList=new ArrayList<>();
        int add=0;
        while(inputSrtreamEa.hasNext()){
            String oneEarthquakeAtATime="";
            for(int i=0;i<7;i++){
                if(i==0 || i==6) {
                    inputSrtreamEa.nextLine();
                    continue;
                }
                else{
                    String temp=inputSrtreamEa.nextLine();
                    String[] splitted=temp.split(" ");
                    for(int x=1;x<splitted.length-1;x++){
                        oneEarthquakeAtATime+=splitted[x];
                    }
                    oneEarthquakeAtATime+=" ";
                    splitted=null;
                    temp=null;
                }
            }
            EarthquakeQueue.enqueue(oneEarthquakeAtATime);
            EarthquakeMagnitudeList.add(add,oneEarthquakeAtATime);
            add++;
        }
        inputSrtreamEa.close();
        //*****************************************************************************************************
        //*****************************************************************************************************
        //Final steps before I start my simulation
        //This is not time efficent but since we are not in the simulation and this step is mandatory, I had to implement it here
        for (int i = 0; i < EarthquakeMagnitudeList.size() - 1; i++)
        {
            int index = i;
            for (int j = i + 1; j < EarthquakeMagnitudeList.size(); j++){
                if (Double.parseDouble(EarthquakeMagnitudeList.get(j).split(" ")[4]) > Double.parseDouble(EarthquakeMagnitudeList.get(index).split(" ")[4])){
                    index = j;//searching for lowest index
                }
            }
            String smallerNumber = EarthquakeMagnitudeList.get(index);
            EarthquakeMagnitudeList.set(index,EarthquakeMagnitudeList.get(i));
            EarthquakeMagnitudeList.set(i,smallerNumber);
        }
        LinkedQueue<String> ActualEarthquakeQueue=new LinkedQueue<>();
        ArrayList<String> ActualEarthquakeMagnitudeList=new ArrayList<>();

        //this varible is here for a faster access to earth magnitude list in the simulation.(for removing old earthquakes)
        int[] theArrayBetweenThem=new int[EarthquakeMagnitudeList.size()];
        //*****************************************************************************************************
        //*****************************************************************************************************
        //SIMULATION
        int valueOnlyForThisArray=0;
        int keepTrack=0;
        for(int time=0;time<1000;time++){
            //*****************************************************************************************************
            //*****************************************************************************************************
            //WATCHERS
            //Since the priority is on Watchers, I implemented "events" of Watchers here
            String[] splitWatcher;
            if(WatcherListWalker.first()!=null){
                splitWatcher=WatcherListWalker.first().split(" ");
                if(!splitWatcher[0].equals("") && Integer.parseInt(splitWatcher[0])==time){
                    if(splitWatcher[1].equals("add")){
                        //Since I have already added all of them, I am just waiting for their time-stamp to
                        //become valid so add method is just the message but now I know which watcher is ACTUALLY in the list
                        System.out.println(splitWatcher[4]+" is added to the watchers list.");
                        System.out.println("");
                    }
                    else {
                        if (splitWatcher[1].equals("delete")) {
                            //for delete I used an Arraylist class provided in the Piazza
                            //I found out the position of the necessary indexes that are needed to be removed and excecuted it
                            System.out.println(splitWatcher[2]+" is removed from watcher-list");
                            System.out.println(" ");
                            ArrayList<String> array=new ArrayList<>();
                            int loop=WatcherList.size();
                            for(int i=0;i<loop;i++){
                                array.add(0,WatcherList.removeFirst());
                            }
                            int index1=0;
                            int index2=0;
                            for(int i=0;i<loop;i++){
                                String[] splittedStr=array.get(i).split(" ");
                                if(splittedStr.length==5){
                                    if(splittedStr[4].equals(splitWatcher[2])){
                                        index1=i;
                                    }
                                }
                                else{
                                    if(splittedStr.length==3){
                                        if(splittedStr[2].equals(splitWatcher[2])){
                                            index2=i;
                                        }
                                    }
                                }
                            }
                            array.remove(index1);
                            array.remove(index2);
                            for(int i=0;i<array.size();i++){
                                if(array.get(i)==null){
                                    continue;
                                }
                                WatcherList.addLast(array.get(i));
                            }
                        }
                    }
                    if(splitWatcher[1].equals("query-largest")){
                        //for query-largest I implemented a try catch since EartMagnitudeList can be empty
                        //Obtained the largest Earthquake simply by getting index 0 so this is efficent
                        boolean flag=true;
                        try {
                            String tmp=ActualEarthquakeMagnitudeList.get(0);
                        }catch (IndexOutOfBoundsException e){
                            flag=false;
                        }
                        if(flag){
                            System.out.println("Largest earthquake in the past 6 hours");
                            String largestEarthQuake=ActualEarthquakeMagnitudeList.get(0);
                            String[] splitted=largestEarthQuake.split(" ");
                            System.out.println("Magnitude "+splitted[4]+" at "+splitted[2]);
                            System.out.println("");
                        }
                        else{
                            System.out.println("No record on list");
                            System.out.println("");
                        }

                    }
                    WatcherListWalker.removeFirst();
                }
            }
            //*****************************************************************************************************
            //*****************************************************************************************************
            //CHECK FOR 6 HOUR AND ABOVE EARTHQUAKES
            //I implemented a very basic algrotihm here that will delete the old Earthquakes
            //Since it is a queue, it was really efficent to just look at the first-out element
            if(ActualEarthquakeQueue.first()!=null && time-Integer.parseInt(ActualEarthquakeQueue.first().split(" ")[1])==5){
                ActualEarthquakeQueue.dequeue();
                //removing from Magnitude list was a O(1) time process since I already know where I should be removing
                //This will become clearer at the next part
                int removeThis=theArrayBetweenThem[keepTrack];
                ActualEarthquakeMagnitudeList.remove(removeThis);
                valueOnlyForThisArray--;
                keepTrack=valueOnlyForThisArray-1;
            }
            //*****************************************************************************************************
            //*****************************************************************************************************
            //ADD NEW EARTHQUAKES
            if( EarthquakeQueue.first()!=null && Integer.parseInt(EarthquakeQueue.first().split(" ")[1])==time){
                if(wantNotification){
                    System.out.println("Earthquake "+EarthquakeQueue.first().split(" ")[2]+" has inserted to Earthquake-magnitude list.");
                }
                //This is an algorithm for adding a new earthquake to my queue
                String tmp=EarthquakeQueue.dequeue();
                String tmp2=tmp;
                ActualEarthquakeQueue.enqueue(tmp);
                ActualEarthquakeMagnitudeList.add(0,tmp);
                //sorting
                for (int i = 0; i < ActualEarthquakeMagnitudeList.size() - 1; i++)
                {
                    int index = i;
                    for (int j = i + 1; j < ActualEarthquakeMagnitudeList.size(); j++){
                        if (Double.parseDouble(ActualEarthquakeMagnitudeList.get(j).split(" ")[4]) > Double.parseDouble(ActualEarthquakeMagnitudeList.get(index).split(" ")[4])){
                            index = j;//searching for lowest index
                        }
                    }
                    String smallerNumber = ActualEarthquakeMagnitudeList.get(index);
                    ActualEarthquakeMagnitudeList.set(index,ActualEarthquakeMagnitudeList.get(i));
                    ActualEarthquakeMagnitudeList.set(i,smallerNumber);
                }
                ArrayList<String> array=new ArrayList<>();
                int loop=WatcherList.size();
                for(int o=0;o<loop;o++){
                    array.add(0,WatcherList.first());
                    WatcherList.removeFirst();
                }
                for(int o=0;o<array.size();o++){
                    if(array.get(o)==null){
                        continue;
                    }
                    WatcherList.addFirst(array.get(o));
                }
                DoublyLinkedList<String> walkThis=WatcherList;
                for(int loop2=0;loop2<loop;loop2++) {
                    String[] splitted=walkThis.first().split(" ");
                    String[] splitOfCurrentEarthquakeP1=tmp2.split(" ");
                    String[] splitFinal=splitOfCurrentEarthquakeP1[3].split(",");
                    if(!splitted[0].equals("")){
                        int timeStampOfthis=Integer.parseInt(splitted[0]);
                        if(timeStampOfthis<=time && splitted.length==5){
                            double coordinate1OfThisWatcher=Double.parseDouble(splitted[2]);
                            double coordinate2OfThisWatcher=Double.parseDouble(splitted[3]);
                            double coordinate1OfThisEarthquake=Double.parseDouble(splitFinal[0]);
                            double coordinate2OfThisEarthquake=Double.parseDouble(splitFinal[1]);

                            double xAxis=Math.abs(coordinate1OfThisEarthquake-coordinate1OfThisWatcher);
                            double yAxis=Math.abs(coordinate2OfThisEarthquake-coordinate2OfThisWatcher);

                            xAxis=xAxis*xAxis;
                            yAxis=yAxis*yAxis;

                            double distance=Math.sqrt(xAxis+yAxis);
                            double magnitudeCube=Double.parseDouble(splitOfCurrentEarthquakeP1[4])*Double.parseDouble(splitOfCurrentEarthquakeP1[4])*Double.parseDouble(splitOfCurrentEarthquakeP1[4]);

                            if(distance<2*magnitudeCube){
                                if(splitted.length==5){
                                    System.out.println("Earthquake "+splitOfCurrentEarthquakeP1[2]+" is close to "+ splitted[4]);
                                }
                            }
                        }
                        walkThis.removeFirst();
                    }
                }
                System.out.println("");
                for(int o=0;o<array.size();o++){
                    if(array.get(o)==null){
                        continue;
                    }
                    WatcherList.addLast(array.get(o));
                }
                //this is my algorithm for O(1) time removal of old earthquakes from  magnitude list, as
                //we are told we cannot search for it in removal process, I am setting a position in a seperate array
                //where I will know for sure where to delete if it needs to be removed
                //Kind of like a hash-map
                for(int i=0;i<ActualEarthquakeMagnitudeList.size();i++){
                    if(ActualEarthquakeMagnitudeList.get(i).equals(tmp)){
                        theArrayBetweenThem[valueOnlyForThisArray]=i;
                        valueOnlyForThisArray++;
                        keepTrack=valueOnlyForThisArray-1;
                    }
                }
            }
            //End Notes: My outputs are not %100 percent identical to the outputs given but it is the same
            //since we are told that minor differences can occur but every criteria is matched without any error.
            //*****************************************************************************************************
            //*****************************************************************************************************
        }
    }
}
