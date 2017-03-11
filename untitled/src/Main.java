/**
 * Главный класс программы
 * Created by rq0 on 06.03.2017.
 */

import org.apache.commons.cli.*;

import java.util.ArrayList;


public class Main {
    private static Options options;
    private static CommandLine line;
    private static ArrayList <User> users;
    private static ArrayList<Resource> resources;
    private static CommandLineParser parser;
    public static ArrayList<Account> accounts;

    public static void main(String args[]) {
        System.out.println("Create completed");
        User first = new User(0,"FirstLogin","FirstPass");
        User sec = new User(1,"SecLogin","SecPass");
        users = new ArrayList<>();
        users.add(first);
        users.add(sec);

        resources = new ArrayList<>();
        Resource resource = new Resource(0,"google.com",first,Role.READ.ordinal());
        resources.add(resource);

        accounts = new ArrayList<>();


        try {
            Validator(args);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
enum Role{
        READ, WRITE, EXECUTE
}
    private static void Validator(String[] args) throws ParseException {
        boolean authentication = false, authorisation=false, accounting=false;
        AAAService aaaService = new AAAService();
        UserInput userInput = new UserInput();
        parser = new GnuParser();
        //добавление всех возможных параметров
        options = new Options();
        options.addOption("login", "login", true, "Логин пользователя");
        options.addOption("pass", "password", true, "Пароль пользователя");
        options.addOption("role", "role", true, "Роль пользователя на выбранном ресурсе");
        options.addOption("res", "resource", true, "Адрес ресурса");
        options.addOption("ds", "DateStart", true, "Дата начала");
        options.addOption("de", "DateEnd", true, "Дата окончания");
        options.addOption("vol", "volume", true, "Объем");
        options.addOption("h", "help", false, "Cправка");
        //получение входных параметров
        line = parser.parse(options, args);
        //Костылина на запуск без параметров или с неизвестными параметрами
        boolean NoParams = (!line.hasOption("login") && !line.hasOption("password") &&
                !line.hasOption("role") && !line.hasOption("resource") &&
                !line.hasOption("DateStart") && !line.hasOption("DateEnd") && !line.hasOption("vol"));
//добавить exception на пустые параметры

        if(line.hasOption("h") || NoParams) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("gl", options);
            System.exit(0);
        }

        int inputUserId = -1;
        if(line.hasOption("login")){
            userInput.login = line.getOptionValue("login");
            inputUserId = aaaService.FindUser(users,userInput);
        }

        if(line.hasOption("pass")){
           userInput.pass = line.getOptionValue("pass");
           //отправляем id, хранящийся у пользователя, а используем как номер в списке, может сломаться
           authentication = aaaService.CheckPass(users,userInput,inputUserId);
        }

        if (authentication) {
            System.out.println("Authentication complete");
        }

        if(line.hasOption("res") && line.hasOption("role")){
            userInput.res = line.getOptionValue("res");
            //костыли с ролью(отменяются, придумать как заменить на свич)

            if(Role.EXECUTE.toString().equals(line.getOptionValue("role"))){
                userInput.role="2";
            }
            else if(Role.WRITE.toString().equals(line.getOptionValue("role"))){
                userInput.role="1";
            }
            else if(Role.READ.toString().equals(line.getOptionValue("role"))){
                userInput.role="0";
            }
            else {
                System.exit(3);
            }
            authorisation=aaaService.CheckRole(users, resources,userInput);
            if (authorisation) {
                System.out.println("Authorisation complete");
            }
        }

        if(line.hasOption("ds")&&line.hasOption("de")&&line.hasOption("vol")&&(authorisation)){
            userInput.vol=line.getOptionValue("vol");
            userInput.ds=line.getOptionValue("ds");
            userInput.de = line.getOptionValue("de");

            accounting = aaaService.AddAccount(users,userInput);

            if (accounting) {
                System.out.println("Accounting complete");
            }
            System.out.println(aaaService.GetAccounts());
        }
    }

//Если понадобится регистрация пользователей
    private static void addUser() {
        if(line.hasOption("-login") && line.hasOption("-pass")) {
            User NewUser = new User(users.size()+1, line.getOptionValue("login"), line.getOptionValue("pass"));
            users.add(NewUser);
        }
    }
//Вывод логинов и паролей (всех)
private static void getUsers(ArrayList<User> users) {
        for (User user:
                Main.users) {
            System.out.println(user.login + user.pass);
        }
    }
}

