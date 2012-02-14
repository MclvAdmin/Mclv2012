/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.math;

/**
 *
 * @author Alex Roman 
 */
public class Matrix {
    private double[][] matrix;
    private int rows = 0;
    private int columns = 0;
    public Matrix(){
        matrix = new double[0][0];
    }
    public Matrix(double[][] data){
        matrix = data;
        rows = matrix.length;
        columns = matrix[0].length;
    }
    public int columns(){
        return columns;
    }
    public int rows(){
        return rows;
    }
    public double entry(int row, int column){
        return matrix[row][column];
    }
    public double[] row(int row){
        return matrix[row];
    }
    public double[] column(int column){
        double[] ret = new double[rows];
        for(int i = 0; i<rows; i++){
            ret[i] = entry(i,column);
        }
        return ret;
    }
    public Matrix getFrom(int rowA, int colA, int rowB, int colB){
        double[][] newData = new double[Math.abs(rowB - rowA)][Math.abs(colB - colA)];
        for(int row = rowA; Math.abs(row - rowB)>0;row = step(row,rowB)){
            for(int col = colA; Math.abs(col - colB)>0;col = step(col,colB)){
                newData[row][col] = matrix[row][col];
            }
        }
        return new Matrix(newData);
    }
    public int step(int a, int b){
        if(a > b){
            return a--;
        }
        else if(a < b){
            return a++;
        }
        else{
            return a;
        }
    }
    public void setColumn(double[] data, int column){
        for(int row = 0; row<rows; row++){
            setEntry(data[row],row,column);
        }
    }
    public void transpose(){
        for(int row = 0; row<rows; row++){
            for(int col = 0; col<columns; col++){
                double a = matrix[row][col];
                matrix[row][col] = matrix[col][row];
                matrix[col][row] = a;
            }
        }
    }
    public void scalarMultiple(double a){
        for(int row = 0; row<rows; row++){
            for(int col = 0; col<columns; col++){
                matrix[row][col] *= a;
            }
        }
    }
    public void setEntry(double entry,int row, int col){
        matrix[row][col] = entry;
    }
    public void changeData(double[][] newData){
        matrix = newData;
        rows = matrix.length;
        columns = matrix[0].length;
    }
    public Matrix inverse(){
        if(rows == columns){
            Matrix cofactors = cofactorMatrix();
            cofactors.transpose();
            cofactors.scalarMultiple(determinant());
            return cofactors;
        }
        return null;
    }
    public double determinant(){
        if(rows>2 || columns>2){
            return cofactorExpRow(0);
        }
        else if(rows == 1 && columns == 1){
            return entry(1,1);
        }
        else{
            return matrix[0][0]*matrix[1][1] - matrix[0][1]*matrix[1][0];
        }
    }
    public void matrixMultiple(Matrix a){
        if(columns == a.rows()){
            double[][] newMatrix = new double[rows][a.columns()];
            for(int row = 0; row<rows; row++){
                for(int col = 0; col<a.columns(); col++){
                    newMatrix[row][col] = dotProduct(row(row),a.column(col));
                }
            }
            changeData(newMatrix);
        }
        else{
            throw new NullPointerException();
        }
    }
    public void print(){
        for(int row = 0; row<rows; row++){
            System.out.print(row + 1 + " [");
            for(int col = 0; col<columns; col++){
                System.out.print(" " + entry(row,col));
            }
            System.out.print(" ]\n");
        }
    }
    public String toString(){
        String ret = "";
        int spaces = digits(rows);
        for(int row = 0; row<rows; row++){
            ret += Integer.toString(row + 1);
            for(int i = 0; i<(spaces - digits(row)); i++){
                ret+= " ";
            }
            ret += " [";
            for(int col = 0; col<columns; col++){
                ret += " " + Double.toString(entry(row,col));
            }
            ret += " ]\n";
        }
        return ret;
    }
    public static Matrix identity(int size){
        double[][] id = new double[size][size];
        for(int row = 0; row<size; row++){
            for(int col = 0; col<size; col++){
                if(row == col){
                    id[row][col] = 1;
                }
                else{
                    id[row][col] = 0;
                }
            }
        }
        return new Matrix(id);
    }
    private Matrix cloneMatrix(){
        return new Matrix(matrix);
    }
    protected Object clone(){
        return new Matrix(matrix);
    }
    private Matrix cofactorMatrix(){
        double[][] ret = copy(matrix);
        for(int row = 0; row<rows; row++){
            for(int col = 0; col<columns; col++){
                ret[row][col] = cofactor(row,col);
            }
        }
        return new Matrix(ret);
    }
    private double cofactorExpRow(int row){
        double exp = 0;
        for(int col = 0; col<columns; col++){
            exp += entry(row,col)*cofactor(row,col);
        }
        return exp;
    }
    private double cofactorExpCol(int col){
        double exp = 0;
        for(int row = 0; row<rows; row++){
            exp += entry(row,col)*cofactor(row,col);
        }
        return exp;
    }
    private double cofactor(int row, int col){
        double cofactor = (exclude(row,col)).determinant();
        if((row + col)% 2 != 0){
            cofactor = -cofactor;
        }
        return cofactor;
    }
    private Matrix exclude(int row, int col){
        Matrix ret = this.cloneMatrix();
        ret.excludeRow(row);
        ret.excludeColumn(col);
        return ret;
    }
    private void excludeRow(int row){
        matrix = removeRow(matrix,row);
        rows--;
    }
    
    private void excludeColumn(int col){
        matrix = removeColumn(matrix,col);
        columns--;
    }
    /*
    private double[][] exclude(double[]][] a,int row, int col){
        a = removeRow(row);
        return a;
    }*/
    private static double[][] copy(double[][] a){
        double[][] ret = new double[a.length][a.length];
        System.arraycopy(a, 0, ret, 0, a.length);
        return ret;
    }
    private static double dotProduct(double[] a, double[] b){
        double dotProduct = 0;
        for(int i = 0; i<a.length; i++){
            dotProduct += a[i]*b[i];
        }
        return dotProduct;
    }
    private static double[][] removeRow(double[][] data, int row){
        double[][] ret = new double[data.length - 1][data[0].length];
        int rowIndex = 0;
        for(int i = 0; i<data.length -1; i++){
            if(i == row){
                rowIndex++;
            }
            ret[i] = data[rowIndex];
            rowIndex++;
        }
        return ret;
    }
    private static double[][] removeColumn(double[][] data, int col){
        int colIndex = 0;
        int cols = data[0].length - 1;
        double[][] ret = new double[data.length][cols];
        /*for(int i = 0; i<data.length -1; i++){
            if(i == col){
                colIndex++;
            }
            ret = setCol(ret,i,data,colIndex);
            colIndex++;
        }*/
        for(int row = 0; row<data.length; row++){
            colIndex = 0;
            for(int column = 0; column<cols; column++){
                if(column == col){
                    colIndex++;
                }
                ret[row][column] = data[row][colIndex];
                colIndex++;
            }
        }
        return ret;
    }
    private static double[][] setCol(double[][] dest, int destCol, double[][] src, int srcCol){
        for(int row = 0; row<dest.length; row++){
            dest[row][destCol] = src[row][srcCol];
        }
        return dest;
    }
    private static double[] removeEntry(double[] data,int entry){
        double[] ret = new double[data.length - 1];
        int index = 0;
        for(int i = 0; i<data.length - 1; i++){
            if(index == entry){
                index++;
            }
            ret[i] = data[index];
            index++;
        }
        return ret;
    }
    private static int digits(int a){
        int ret = 0;
        for(int digits = 0; (a%10^digits)>(10^digits);digits++){
            ret = digits;
        }
        return ret;
    }
    public static double[][] emptySquare(int size){
        return new double[size][size];
    }
    /*private static double det(double[][] raw){ @TODO use instance implementation w/constructed submatricies
        double det = 0;
        int rows = raw.length;
        int cols = raw[0].length;
        int detRow = 0;
        return det;
    }
    private static double[][] removeRow(double[][] data, int row){
        int rowIndex = 0;
        double[][] ret = new double[data.length][data[0].length];
        for(int i = 0; i<data.length -1; i++){
            if(i = row){
                i++;
            }
        }
    }*/ 
}
