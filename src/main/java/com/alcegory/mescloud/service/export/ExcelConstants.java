package com.alcegory.mescloud.service.export;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ExcelConstants {

    public static final String SHEET_NAME_PRODUCTION_ORDERS = "Ordens de Produção";
    public static final String SHEET_NAME_COMPOSED = "Produções Compostas";
    public static final String TABLE_NAME_PRODUCTION = "ProductionOrdersTable";
    public static final String TABLE_NAME_COMPOSED = "ComposedProductionOrdersTable";
    public static final String TABLE_STYLE = "TableStyleMedium9";

    private ExcelConstants() {
    }

    public static String[] getHeaders(boolean withHits, boolean isCompleted) {
        List<String> headersList = new ArrayList<>();

        if (isCompleted) {
            headersList.add("Lote Final");
        }

        String[] commonHeaders = {
                "Produção Composta",
                "Lote de Entrada",
                "Proveniência",
                "Calibre",
                "Classe",
                "Lavação",
                "Quantidade",
                "Amostra",
                "Criação da composta"
        };

        headersList.addAll(Arrays.asList(commonHeaders));

        if (withHits) {
            headersList.addAll(Arrays.asList("Hits", "Fiabilidade", "Hits inseridos em"));
        }

        if (isCompleted) {
            headersList.add("Status");
            headersList.add("Resolvido em");
        }

        return headersList.toArray(new String[0]);
    }

    public static String[] getHeaders(boolean isCompleted) {
        List<String> headersList = new ArrayList<>();

        String[] commonHeaders = {
                "Equipamento",
                "Ordem de Produção",
                "IMS",
                "Lote de Entrada",
                "Proveniência",
                "Calibre",
                "Classe",
                "Lavação",
                "Quantidade",
                "Início de Produção",
                "Conclusão de Produção"
        };

        // Add common headers
        headersList.addAll(Arrays.asList(commonHeaders));

        if (isCompleted) {
            headersList.add(1, "Produção Composta");
        }

        return headersList.toArray(new String[0]);
    }

    public static String[] getComposedHeaders() {
        return getHeaders(true, true);
    }

    public static String[] getProductionOrderHeaders() {
        return getHeaders(true);
    }
}
