package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Main {

    private static boolean clientRunning;

    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 23456);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            clientRunning = true;
            System.out.print("Enter action (1 - get the file, 2 - create a file, 3 - delete the file): ");
            Scanner scanner = new Scanner(System.in);

            Thread fromConsoleToServer = new Thread(() -> {
                //while (clientRunning) {
                    try {
                        String msg = scanner.next();
                        String request = "";
                        if (msg.equals("2")) {
                            System.out.print("Enter filename: ");
                            String filename = scanner.next();
                            scanner.nextLine();
                            System.out.print("Enter file content: ");
                            String fileContent = scanner.nextLine();

                            System.out.println(request);
                            request = "PUT~" + filename + "~" + fileContent;
                        } else if (msg.equals("1")) {
                            System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");
                            int choice = scanner.nextInt();
                            String filename;
                            if (choice == 1) {
                                System.out.print("Enter filename: ");
                            } else {
                                System.out.print("Enter id: ");
                            }
                            filename = scanner.next();


                            boolean containsFile = false;
                            if (choice == 2) {
                                containsFile = server.Main.database.filenames.containsFileByID(Integer.parseInt(filename, 10));
                            } else {
                                containsFile = server.Main.database.filenames.containsFile(filename);
                            }

                            if (containsFile) {
                                System.out.print("The file was downloaded! Specify a name for it: ");
                                String outputName = scanner.next();

                                request = "GET " + filename + " " + choice + " " + outputName;
                            } else {
                                request = "GET " + filename + " " + choice;
                            }


                        } else if (msg.equals("3")) {
                            System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");
                            int choice = scanner.nextInt();
                            String filename;
                            if (choice == 1) {
                                System.out.print("Enter filename: ");
                            } else {
                                System.out.print("Enter id: ");
                            }
                            filename = scanner.next();

                            request = "DELETE " + filename + " " + choice;
                        } else if (msg.equals("exit")) {

                            request = "exit";
                        }

                        output.writeUTF(request);


                       // clientRunning = false;
                    } catch (IOException e) {
                     //   break;
                    }
               // }
            });

            Thread fromServerToConsole = new Thread(() -> {
                while (clientRunning) {
                    try {
                        System.out.println(input.readUTF());
                    } catch (IOException e) {
                        break;
                    }
                }
            });

            fromConsoleToServer.start();
            fromServerToConsole.start();

            fromConsoleToServer.join();
            fromServerToConsole.join();

        } catch (Exception e) {

        }
    }


}