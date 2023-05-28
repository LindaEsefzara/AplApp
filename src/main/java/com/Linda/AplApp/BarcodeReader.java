package com.Linda.AplApp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

public class BarcodeReader {

    public static void main(String[] args) {
        String url = "https://zxing.org/w/chart?cht=qr&chs=350x350&chld=L&choe=UTF-8&chl=BEGIN%3AVEVENT%0D%0ASUMMARY%3AStudent+presence%0D%0ADTSTART%3BVALUE%3DDATE%3A20230527%0D%0ADTEND%3BVALUE%3DDATE%3A20230701%0D%0ALOCATION%3AStudent+%26+Teacher%0D%0ADESCRIPTION%3AN%C3%A4rvaro+bekr%C3%A4ftelse+%0D%0AEND%3AVEVENT%0D%0A";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            byte[] barcodeData = response.getBody();

            // Gör något med streckkoden eller dess bilddata här
        } else {
            System.out.println("Det uppstod ett fel vid hämtning av streckkoden.");
        }
    }
    @GetMapping("/barcode")
    public ResponseEntity<byte[]> getBarcode() {
        String url = "https://zxing.org/w/chart?cht=qr&chs=350x350&chld=L&choe=UTF-8&chl=BEGIN%3AVEVENT%0D%0ASUMMARY%3AStudent+presence%0D%0ADTSTART%3BVALUE%3DDATE%3A20230527%0D%0ADTEND%3BVALUE%3DDATE%3A20230701%0D%0ALOCATION%3AStudent+%26+Teacher%0D%0ADESCRIPTION%3AN%C3%A4rvaro+bekr%C3%A4ftelse+%0D%0AEND%3AVEVENT%0D%0A";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

