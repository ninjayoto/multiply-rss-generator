/*
 * This file is part of MultiplyRSSGenerator.
 * 
 * MultiplyRSSGenerator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Hidayat Febiansyah
 * @email havban@gmail.com
 * @link http://code.google.com/p/multiply-rss-generator/
 * @year 2012
 */
package febi.rssgen.multiply;

import febi.com.log.Logger;
import febi.rssgen.com.multiply.gui.TaskItem;
import febi.rssgen.com.multiply.gui.TaskViewer;
import febi.rssgen.com.multiply.rss.MultiplyPageProcessor;
import febi.rssgen.com.rss.PageData;
import febi.rssgen.com.rss.PageProcessor;
import febi.rssgen.com.rss.RSSGenerator;
import febi.rssgen.com.rss.RSSItem;
import febi.rssgen.com.rss.RSSTerm;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author itrc169
 */
public class MultiplyRSSGeneratorHandler implements Runnable {
    
    public static final int MAX_FILE_LENGTH = 10000000;
    public static final String FILE_EXT = ".xml";
    public static final int MAX_TRIAL = 3;
    private static Calendar cal;
    private static String mUser;
    private static boolean journal,
            reviews,
            recipes,
            notes,
            photos;
    private static String outputFolder;
    private static JComponent[] disabledComps;
    private static JComponent[] enabledComps;
    private static JFrame window;
    private static TaskViewer taskViewer;
    private TaskItem journalTask, reviewsTask, notesTask, recipesTask, photosTask;
    private static Date startDate, endDate;
    
    public static void startProcess(
            String mUserPar,
            boolean journalPar,
            boolean reviewsPar,
            boolean recipesPar,
            boolean notesPar,
            boolean photosPar,
            String outputFolderPar,
            Date startDatePar,
            Date endDatePar,
            JComponent[] disabledCompsPar,
            JComponent[] enabledCompsPar,
            JFrame windowPar,
            TaskViewer taskViewerPar) {
        
        mUser = mUserPar;
        journal = journalPar;
        reviews = reviewsPar;
        recipes = recipesPar;
        notes = notesPar;
        photos = photosPar;
        outputFolder = outputFolderPar;
        startDate = startDatePar;
        endDate = endDatePar;
        disabledComps = disabledCompsPar;
        enabledComps = enabledCompsPar;
        window = windowPar;
        taskViewer = taskViewerPar;
        
        Thread t = new Thread(new MultiplyRSSGeneratorHandler());
        t.start();
    }
    
    @Override
    public void run() {
        
        if (disabledComps != null) {
            for (JComponent c : disabledComps) {
                c.setEnabled(false);
            }
        }
        if (enabledComps != null) {
            for (JComponent c : enabledComps) {
                c.setEnabled(true);
            }
        }

        //real process
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH-mm-ss_");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strTimestamp = formatter.format(new Date());
        
        Logger.outputMessageLn("Start Processing on: " + formatter.format(new Date()));
        Logger.outputMessageLn("\tuser:\t" + mUser);
        Logger.outputMessageLn("\tJournal:\t" + (journal ? "Yes" : "No"));
        Logger.outputMessageLn("\tReviews:\t" + (reviews ? "Yes" : "No"));
        Logger.outputMessageLn("\tRecipes:\t" + (recipes ? "Yes" : "No"));
        Logger.outputMessageLn("\tNotes:\t" + (notes ? "Yes" : "No"));
        Logger.outputMessageLn("\tPhotos:\t" + (photos ? "Yes" : "No"));
        Logger.outputMessageLn("\tOutput folder:\t" + outputFolder);
        Logger.outputMessageLn("\tDate range:\t" + (startDate == null ? "ALL"
                : dateFormat.format(startDate) + " to " + dateFormat.format(endDate)));
        Logger.outputMessageLn("\n");

        //journal
        if (journal) {
            Logger.outputMessageLn("--> 'Journal' is selected. Processing..");
            journalTask = taskViewer.getTaskByName("journal");
            processFolder(mUser, MultiplyPageProcessor.JOURNAL, outputFolder, strTimestamp, journalTask);
            Logger.outputMessageLn("--> .. done");
        }

        //reviews
        if (reviews) {
            Logger.outputMessageLn("--> 'Review' is selected. Processing..");
            reviewsTask = taskViewer.getTaskByName("reviews");
            processFolder(mUser, MultiplyPageProcessor.REVIEWS, outputFolder, strTimestamp, reviewsTask);
            Logger.outputMessageLn("--> .. done");
        }

        //recipes
        if (recipes) {
            Logger.outputMessageLn("--> 'Recipes' is selected. Processing..");
            recipesTask = taskViewer.getTaskByName("recipes");
            processFolder(mUser, MultiplyPageProcessor.RECIPES, outputFolder, strTimestamp, recipesTask);
            Logger.outputMessageLn("--> .. done");
        }

        //notes
        if (notes) {
            Logger.outputMessageLn("--> 'Notes' is selected. Processing..");
            notesTask = taskViewer.getTaskByName("notes");
            processFolder(mUser, MultiplyPageProcessor.NOTES, outputFolder, strTimestamp, notesTask);
            Logger.outputMessageLn("--> .. done");
        }

        //photos
        if (photos) {
            Logger.outputMessageLn("--> 'Photos' is selected. Processing..");
            photosTask = taskViewer.getTaskByName("photos");
            processFolder(mUser, MultiplyPageProcessor.PHOTOS, outputFolder, strTimestamp, photosTask);
            Logger.outputMessageLn("--> .. done");
        }
        
        Logger.outputMessageLn("Process Finish on : " + formatter.format(new Date()));

        //end process
        if (disabledComps != null) {
            
            for (JComponent c : disabledComps) {
                c.setEnabled(true);
            }
        }
        if (enabledComps != null) {
            
            for (JComponent c : enabledComps) {
                c.setEnabled(false);
            }
        }
        
        
        JOptionPane.showMessageDialog(window, "Finish processing all items...");
        
    }
    
    private static void processFolder(String mUser,
            String folder, String outputFolder, String strTimestamp,
            TaskItem item) {
        
        ArrayList<RSSTerm> rssList = new ArrayList();
        String rawString;
        
        PageProcessor pageProc = new MultiplyPageProcessor(mUser, folder,
                taskViewer.getTaskByName("summary"), startDate, endDate);
        Logger.outputMessageLn("=--> Obtaining page list..");
        item.setDesc("starting process..\n -- it may take several minutes."
                + "\n -- check on output area for progress.");
        ArrayList<PageData> pageList = pageProc.getPageResults();
        RSSGenerator rssGen = pageProc.getRSSGenerator();
        
        int counter = 1;
        int totalPage = pageList.size();
        
        item.setDesc("has " + totalPage + " number of page(s).");
        //retrying till limit
        int trialCounter;
        
        for (PageData page : pageList) {
            Logger.outputMessageLn("Fetching post: " + page.getUrl());
            trialCounter = 0;
            while(trialCounter < MAX_TRIAL){
                try{
                    trialCounter++;
                    rawString = PageProcessor.getPageData(page.getUrl());

                    Logger.outputMessageLn("=--> parsing page " + counter + " of " + totalPage + " page(s)");
                    rssList.addAll(rssGen.getParsedItems(rawString));
                    break;
                }catch(NullPointerException npe){
                    Logger.outputMessage("**** Error parsing "+trialCounter+", refetching page: "+page.getUrl());
                }
            }
            
            counter++;
        }
        
        if (startDate != null && folder.equals(MultiplyPageProcessor.PHOTOS)) {
            item.setDesc("Filtering photos by preferred date range.");
            ArrayList<RSSTerm> listToRemove = new ArrayList();
            for (Iterator<RSSTerm> it = rssList.iterator(); it.hasNext();) {
                RSSTerm photoTerm = it.next();
                if (photoTerm instanceof RSSItem) {
                    RSSItem photoItem = (RSSItem) photoTerm;
                    if (photoItem.getDate().after(endDate)
                            || photoItem.getDate().before(startDate)) {
                        //remove from list
                        listToRemove.add(photoTerm);
                    }
                }
            }
            
            rssList.removeAll(listToRemove);
            item.setDesc(rssList.size()+" page(s) is/are left.");

        }

        //construct output file name
        String outFilename = outputFolder + File.separator + strTimestamp
                + mUser + ".multiply." + folder;
        
        Logger.outputMessageLn("--> Writing " + folder + " to " + outFilename + " * ..");
        item.setDesc("file(s) is/are being written to " + outFilename);

        //writing journal
        writeItems(rssList, rssGen, outFilename);
        
        item.setDesc("finished process!\n-------------");
        
    }

    //write file
    private static void writeItems(ArrayList<RSSTerm> items,
            RSSGenerator rssGen, String filePath) {
        //size counter
        int counter = 1;
        StringBuilder rssString = new StringBuilder();
        String outputFile;

        //for every list
        //obtain string data
        rssString.append(rssGen.getHeader());
        boolean doWrite = true;
        boolean isParted = false;
        
        for (RSSTerm item : items) {
            rssString.append(item.getFormatted());
            doWrite = true;
            
            if (rssString.length() > MAX_FILE_LENGTH) {
                rssString.append(rssGen.getFooter());
                
                outputFile = filePath + ".part" + counter + FILE_EXT;
                
                performWrite(outputFile, rssString.toString());
                
                rssString = new StringBuilder();
                rssString.append(rssGen.getHeader());
                
                counter++;
                
                doWrite = false;
                isParted = true;
            }
        }
        
        if (doWrite) {
            rssString.append(rssGen.getFooter());
            
            if (isParted) {
                outputFile = filePath + ".part" + counter + FILE_EXT;
            } else {
                outputFile = filePath + FILE_EXT;
            }
            
            performWrite(outputFile, rssString.toString());
        }
        
    }
    
    private static void performWrite(String filePath, String content) {
        BufferedWriter buffWriter = null;
        try {
            buffWriter = new BufferedWriter(new FileWriter(filePath, false));
            buffWriter.write(content);
            buffWriter.flush();
            buffWriter.close();
        } catch (IOException ex) {
            Logger.outputMessageLn(ex.getMessage());
        } finally {
            try {
                buffWriter.close();
            } catch (IOException ex) {
                Logger.outputMessageLn(ex.getMessage());
            }
        }
    }
}
