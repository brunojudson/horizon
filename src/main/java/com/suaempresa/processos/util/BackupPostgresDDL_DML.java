package com.suaempresa.processos.util;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BackupPostgresDDL_DML {

    public static void realizarBackupCompleto(String host, String port, String database, String user, String password, String backupDir) {
        // Nome do arquivo com data
        String dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String nomeArquivo = "backup_completo_" + dataAtual + ".sql";
        String caminhoArquivo = backupDir + File.separator + nomeArquivo;

        // Comando para backup plain (DDL + DML)
        String comando = String.format(
            "pg_dump -h %s -p %s -U %s -F p -b -v -f \"%s\" %s",
            host, port, user, caminhoArquivo, database
        );

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd.exe", "/c", comando); // Para Windows
        processBuilder.environment().put("PGPASSWORD", password);
        System.out.println("Comando executado: " + comando);

        try {
            File diretorio = new File(backupDir);
            if (!diretorio.exists()) {
                if (diretorio.mkdirs()) {
                    System.out.println("Diretório de backup criado: " + backupDir);
                } else {
                    System.err.println("Erro ao criar o diretório de backup: " + backupDir);
                    return;
                }
            }

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                File arquivoBackup = new File(caminhoArquivo);
                if (arquivoBackup.exists() && arquivoBackup.isFile()) {
                    System.out.println("Backup completo realizado com sucesso: " + caminhoArquivo);
                } else {
                    System.err.println("Erro: O comando foi executado, mas o arquivo de backup não foi gerado.");
                }
            } else {
                System.err.println("Erro ao realizar o backup. Código de saída: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Erro ao executar o backup: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        String port = "5432";
        String database = "processos_db";
        String user = "postgres";
        String password = "admin";
        String backupDir = "C:\\Backup\\SQL";

        realizarBackupCompleto(host, port, database, user, password, backupDir);
    }
}