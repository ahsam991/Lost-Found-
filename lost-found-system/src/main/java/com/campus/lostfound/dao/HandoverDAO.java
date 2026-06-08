package com.campus.lostfound.dao;

import com.campus.lostfound.model.Handover;
import java.sql.SQLException;
import java.util.List;

public interface HandoverDAO {
    int createHandover(Handover handover) throws SQLException;
    Handover getHandoverById(int handoverId) throws SQLException;
    Handover getHandoverByClaimId(int claimId) throws SQLException;
    List<Handover> getAllHandovers() throws SQLException;
}
