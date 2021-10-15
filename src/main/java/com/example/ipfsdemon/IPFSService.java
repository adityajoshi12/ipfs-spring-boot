package com.example.ipfsdemon;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class IPFSService implements FileServiceImpl {

    @Autowired
    IPFSConfig ipfsConfig;

    @Override
    public String saveFile(String filePath) {
        try {
            IPFS ipfs = ipfsConfig.ipfs;
            File _file = new File(filePath);
            NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(_file);
            MerkleNode response = ipfs.add(file).get(0);
            System.out.println("Hash (base 58): " + response.hash.toBase58());
            return response.hash.toBase58();
        } catch (IOException ex) {
            throw new RuntimeException("Error whilst communicating with the IPFS node", ex);
        }
    }

    @Override
    public String saveFile(MultipartFile file) {


        try {

            InputStream inputStream = new ByteArrayInputStream(file.getBytes());
            IPFS ipfs = ipfsConfig.ipfs;

            NamedStreamable.InputStreamWrapper is = new NamedStreamable.InputStreamWrapper(inputStream);
            MerkleNode response = ipfs.add(is).get(0);
            System.out.println("Hash (base 58): " + response.name.get() + " - " + response.hash.toBase58());
            return response.hash.toBase58();

        } catch (IOException ex) {
            throw new RuntimeException("Error whilst communicating with the IPFS node", ex);
        }

    }

    @Override
    public byte[] loadFile(String hash) {
        try {

            IPFS ipfs = ipfsConfig.ipfs;
            Multihash filePointer = Multihash.fromBase58(hash);

            return ipfs.cat(filePointer);
        } catch (IOException ex) {
            throw new RuntimeException("Error whilst communicating with the IPFS node", ex);
        }
    }

}
