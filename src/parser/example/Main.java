package parser.example;

import static java.util.Comparator.comparing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;

import java.util.logging.Logger;




public class Main {
    private static PrintWriter printWriter;
    private static StringBuilder stringBuilder;
    static Logger logger = Logger.getLogger("Main.class");
    private static int ID=7;
    private static String packageName;  
    private static String fileName;
    private static String newFileHead="E:/dataSrcTest/";
    private static boolean needMove=true;
    
    public static void main(String[] args) throws IOException {
        stringBuilder = new StringBuilder();
        String outputName="E:/test/SignalResultsTest.csv";
        // This is where the CSV file will be created.
		printWriter = new PrintWriter(new File(outputName));
        // This folder is where the program will be analyzing
		//File folderToAnalyze = new File("E:/test/ooo/sampleMyself");
        //File folderToAnalyze = new File("E:/00-data/00-javadataset/java-small/java-small/test/hadoop");        
        //File folderToAnalyze = new File("E:/dataSet/Java-master");
		File folderToAnalyze = new File("E:/00-data/00-javadataset/java-small/java-small/test/due");
		//File folderToAnalyze = new File("E:/dataSet/JAVADataset-master/JAVADataset-master/src");

        // CSV file headers
        stringBuilder.append("File,Package,Class,ID,Method,ReturnType,Parameters\n");

        //��������ļ��Ѿ�ʱ�յ��ˣ���ôӦ�����Ѿ����ļ��ƶ����ˣ����¶�λ���µ�λ�ü�������
        if(folderToAnalyze.listFiles().length==0){
        	folderToAnalyze=new File(newFileHead);
        	needMove=false;
        	System.out.println("���Ѿ�����ѽ~");
        }
        //������ǿյ�
        else{
        	//passĬ���ƶ������ƶ����ǽ���֮�����Բ���������
        }
        // Let's analyze the files!
        listFilesForFolder(folderToAnalyze);

        // Moves the data to CSV
        printWriter.write(stringBuilder.toString());

        // We're done with printWriter!
        printWriter.close();
        
        System.out.print("please check the file: "+outputName);
    }

    public static void listFilesForFolder(File folder) throws IOException {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                if (fileEntry.getName().endsWith(".java")) {                	
                    parseClassesForFile(fileEntry);                                                          
                }
            }
        }
    }
    
    public static void parseClassesForFile(File file) throws IOException {
        InputStream in = null;
        CompilationUnit cu;
        try {
            in = new FileInputStream(file);
            cu = JavaParser.parse(in);                   
            
        	//ÿ���ض�һ��Java�ļ���Щ�����ᱻ���ã���static�ı�����������
            packageName="";   
            fileName=file.getName();
            new getPackageName().visit(cu, null);
            //new getConstructorName().visit(cu, null);
            new ClassVisitorAdapter().visit(cu, null);      
           //�������Ȼ��������з����ƶ�
            if(needMove){
            	String newPath=newFileHead+packageName.replaceAll("\\.","/")+"/";  
                File newFilePath=new File(newPath);
//                File oldFile=new File("E:/00-data/00-javadataset/java-small/java-small/test/hadoopSmall/AboutBlock.java");
        		File newFile=new File(newPath+fileName); 
                try {
                	                
                		newFilePath.mkdirs();  
                        file.renameTo(newFile);
                   
                } catch (Exception e) {
                    e.printStackTrace();
                } 
            }
                      
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        } finally {
            in.close();
        }
    }
    
    // To get the classes
    private static class ClassVisitorAdapter extends VoidVisitorAdapter<Object> {
        @Override
        public void visit(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Object arg) {
          
        	
            //ͨ���ж��ǲ���һ��method����ȡ�����ķ������ƣ�����ֵ�����β��б�Ĳ��������Ҹ�����һ��idֵ
            if (!classOrInterfaceDeclaration.getMethods().isEmpty()) {           	
                for (MethodDeclaration method : classOrInterfaceDeclaration.getMethods()) {
                    stringBuilder.append(fileName+",");
                    stringBuilder.append(packageName+",");
                    stringBuilder.append(classOrInterfaceDeclaration.getName()+",");
                    stringBuilder.append(ID+",");
                    stringBuilder.append(method.getName()+",");
                    String temp=method.getType()+"";
                    if(temp.contains("/")){                	
                    	String t[]=temp.split("\\n");
                    	stringBuilder.append(t[t.length-1]+",");
                    }
                    else{
                    	stringBuilder.append(method.getType()+",");
                    }
                   
                    ID++;                
                    NodeList<com.github.javaparser.ast.body.Parameter> parametersSmall = method.getParameters();
                    if(parametersSmall.isEmpty()){
                 //pass       
                    }
                    else{                   	
                    	for (com.github.javaparser.ast.body.Parameter parameter : parametersSmall) {
                    		//�������б�ת�����ַ������Ա�֮�����зֺʹ洢����
                    		String parameterString=String.valueOf(parameter.getType());
                    		                    		                   		
                    		//����Ǻ��а�������ֻ������������
                    		String parametersList[]=parameterString.split("\\.");
                    		
                    		if(parametersList.length>0)//��������б��ǿյ����ü��ˣ��������鳤�Ȳ��ܷ���
                    		{                    		
                    			parameterString=parametersList[parametersList.length-1];                   
                    		}
                    		
                    		stringBuilder.append(parameterString+",");
                        }     	
                    	
                    }                   
                    stringBuilder.append("\n");
                   
            }                            
           
        }
    }
 }
      
    private static class getPackageName extends VoidVisitorAdapter<Object>  {
        @Override
        public void visit(PackageDeclaration n, Object arg) {
            packageName=n.getPackageName();
            super.visit(n, arg);
        }
        
    }
    //���캯��������ȡ��
//    private static class getConstructorName extends VoidVisitorAdapter<Object>  {
//    	@Override
//        public void visit(ConstructorDeclaration n, Object arg) {        
//            stringBuilder.append(fileName+",");
//            stringBuilder.append(packageName+",");
//            stringBuilder.append(n.getName()+",");
//            stringBuilder.append(ID+",");
//            stringBuilder.append(n.getName()+","+",");
//            ID++;
//               
//            //��ȡ���캯���Ĳ���
//            for (com.github.javaparser.ast.body.Parameter parameter : n.getParameters()) {
//            	 String parameterString=String.valueOf(parameter.getType());
//            		
//         		//����Ǻ��а�������ֻ������������
//         		String parametersList[]=parameterString.split("\\.");
//         		
//         		if(parametersList.length>0)//��������б��ǿյ����ü��ˣ��������鳤�Ȳ��ܷ���
//         		{                    		
//         			parameterString=parametersList[parametersList.length-1];                   
//         		}         		
//         		stringBuilder.append(parameterString+",");
//            }
//            stringBuilder.append("\n");
//            super.visit(n, arg);
//        }
//    }
}















