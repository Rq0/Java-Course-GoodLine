import org.apache.commons.cli.*;

class Validator {

    private static CommandLine line;

    static void Validate(String[] args,AAAService aaaService) throws ParseException {
        boolean authentication = false;
        boolean authorisation = false;
        UserInput userInput = new UserInput();
        CommandLineParser parser = new DefaultParser();

        //добавление всех возможных параметров
        Options options = new Options();
        options.addOption("login", "login", true, "Логин пользователя");
        options.addOption("pass", "password", true, "Пароль пользователя");
        options.addOption("role", "role", true, "Роль пользователя на выбранном ресурсе");
        options.addOption("res", "resource", true, "Адрес ресурса");
        options.addOption("ds", "DateStart", true, "Дата начала");
        options.addOption("de", "DateEnd", true, "Дата окончания");
        options.addOption("vol", "volume", true, "Объем");
        options.addOption("h", "help", false, "Cправка");

        //получение входных параметров
        try {
            line = parser.parse(options, args);
        } catch (Exception e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("gl", options);
            System.exit(0);
        }

        if (line.hasOption("h") || line.getOptions().length==0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("gl", options);
            System.exit(0);
        }

        if (line.hasOption("login")) {
            userInput.login = line.getOptionValue("login");
            aaaService.FindUser(userInput);

            if (line.hasOption("pass")) {
                userInput.pass = line.getOptionValue("pass");
                authentication = aaaService.CheckPass(userInput);
            }
        }


        if (line.hasOption("res") && line.hasOption("role") && authentication) {
            userInput.res = line.getOptionValue("res");
            //костыли с ролью(отменяются, придумать как заменить на свич)

            if (AAAService.Role.EXECUTE.toString().equals(line.getOptionValue("role"))) {
                userInput.role = "2";
            } else if (AAAService.Role.WRITE.toString().equals(line.getOptionValue("role"))) {
                userInput.role = "1";
            } else if (AAAService.Role.READ.toString().equals(line.getOptionValue("role"))) {
                userInput.role = "0";
            } else {
                System.exit(3);
            }
            authorisation = aaaService.CheckRole(userInput);
        }

        if (line.hasOption("ds") && line.hasOption("de") && line.hasOption("vol") && (authorisation)) {
            userInput.vol = line.getOptionValue("vol");
            userInput.ds = line.getOptionValue("ds");
            userInput.de = line.getOptionValue("de");

            aaaService.AddAccount(userInput);
        }
    }
}